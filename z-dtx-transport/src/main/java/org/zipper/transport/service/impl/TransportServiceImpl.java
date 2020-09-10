package org.zipper.transport.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zipper.helper.data.transport.common.collectors.JobPluginCollector;
import org.zipper.helper.data.transport.core.Engine;
import org.zipper.helper.exception.HelperException;
import org.zipper.helper.util.json.JsonObject;
import org.zipper.helper.web.response.ResponseEntity;
import org.zipper.transport.enums.DbType;
import org.zipper.transport.enums.TransportInstanceStatus;
import org.zipper.transport.enums.TransportScheduleStatus;
import org.zipper.transport.error.TransportError;
import org.zipper.transport.mapper.TransportMapper;
import org.zipper.transport.pojo.dto.TransportQueryParams;
import org.zipper.transport.pojo.entity.DataBase;
import org.zipper.transport.pojo.entity.MySqlDb;
import org.zipper.transport.pojo.entity.Transport;
import org.zipper.transport.pojo.entity.TransportInstance;
import org.zipper.transport.pojo.vo.TransportVO;
import org.zipper.transport.service.DbService;
import org.zipper.transport.service.TransportInstanceService;
import org.zipper.transport.service.TransportService;
import org.zipper.transport.service.rpc.DtxJobService;
import org.zipper.transport.utils.ConfigUtil;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zhuxj
 * @since 2020/08/27
 */
@Service
@Slf4j
public class TransportServiceImpl implements TransportService {

    @Resource
    private TransportMapper transportMapper;
    @Autowired
    private DbService dbService;
    @Autowired
    private DtxJobService dtxJobService;
    @Autowired
    private TransportInstanceService transportInstanceService;

    @Override
    public int addOne(Transport transport) {

        int sourceDbType = Integer.parseInt(transport.getSource().split("-")[0]);
        int sourceDbId = Integer.parseInt(transport.getSource().split("-")[1]);
        DataBase sourceDataBase = dbService.queryOne(sourceDbType, sourceDbId);
        switch (DbType.get(sourceDbType)) {
            case MySql:
                transport.setConfig(ConfigUtil.supplementMySqlReader(transport.getConfig(), (MySqlDb) sourceDataBase));
                break;
        }

        int targetDbType = Integer.parseInt(transport.getTarget().split("-")[0]);
        int targetDbId = Integer.parseInt(transport.getTarget().split("-")[1]);
        DataBase targetDataBase = dbService.queryOne(targetDbType, targetDbId);
        switch (DbType.get(targetDbType)) {
            case MySql:
                transport.setConfig(ConfigUtil.supplementMySqlWriter(transport.getConfig(), (MySqlDb) targetDataBase));
                break;
        }

        return this.transportMapper.insert(transport);
    }

    @Override
    public List<TransportVO> queryByParams(TransportQueryParams params) {
        return this.transportMapper.select(params);
    }

    @Override
    public PageInfo<TransportVO> queryByParams(TransportQueryParams params, Integer pageNum, Integer pageSize) {
        if (pageNum >= 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<TransportVO> list = queryByParams(params);
        return PageInfo.of(list);
    }

    @Override
    public Transport queryOne(Integer id) {
        return this.transportMapper.selectById(id);
    }

    @Override
    public boolean updateOne(Transport transport) {
        int sourceDbType = Integer.parseInt(transport.getSource().split("-")[0]);
        int sourceDbId = Integer.parseInt(transport.getSource().split("-")[1]);
        DataBase sourceDataBase = dbService.queryOne(sourceDbType, sourceDbId);
        switch (DbType.get(sourceDbType)) {
            case MySql:
                transport.setConfig(ConfigUtil.supplementMySqlReader(transport.getConfig(), (MySqlDb) sourceDataBase));
                break;
        }

        int targetDbType = Integer.parseInt(transport.getTarget().split("-")[0]);
        int targetDbId = Integer.parseInt(transport.getTarget().split("-")[1]);
        DataBase targetDataBase = dbService.queryOne(targetDbType, targetDbId);
        switch (DbType.get(targetDbType)) {
            case MySql:
                transport.setConfig(ConfigUtil.supplementMySqlWriter(transport.getConfig(), (MySqlDb) targetDataBase));
                break;
        }
        int record = this.transportMapper.update(transport);
        return record == 1;
    }

    @Override
    @Async("jobRunnerPool")
    public void run(Integer id) {
        Transport transport = queryOne(id);


        //创建实例
        TransportInstance instance = new TransportInstance();
        instance.setTid(Long.valueOf(id));
        instance.setConfig(transport.getConfig());
        instance.setStatus(TransportInstanceStatus.RUNNING.getType());
        long instanceId = transportInstanceService.addOne(instance);

        //补充必要参数
        JsonObject allConfig = JsonObject.newDefault();
        allConfig.set("job", JsonObject.from(transport.getConfig()));
        allConfig.set("job.id", id.toString() + "#" + instanceId);
        try {
            Engine engine = new Engine(allConfig);
            JobPluginCollector collector = (JobPluginCollector) engine.entry();
            instance.setReadCnt(collector.getReadCnt());
            instance.setWriteCnt(collector.getWriteCnt());
            instance.setErrorCnt(collector.getErrorCnt());
            instance.setStartTime(collector.getStartLocalDateTime());
            instance.setEndTime(collector.getEndLocalDateTime());
            instance.setStatus(TransportInstanceStatus.SUCCESS.getType());
        } catch (Exception e) {
            log.error("传输任务执行失败", e);
            instance.setEndTime(LocalDateTime.now());
            instance.setStatus(TransportInstanceStatus.FAIL.getType());
        }

        transportInstanceService.updateOne(instance);
    }

    @Override
    @Transactional
    public void registerJob(Integer id) {
        Transport transport = queryOne(id);
        transport.setRegistered(TransportScheduleStatus.REGISTERED.getType());
        updateOne(transport);
        ResponseEntity resp = dtxJobService.registerJob(transport.getRuleId(), transport.getId());
        if (resp.getCode() != 200) {
            log.error("注册任务至调度中心失败,{}", resp.toString());
            throw HelperException.newException(TransportError.REGISTER_ERROR);
        }
    }

    @Override
    @Transactional
    public void cancelJob(Integer id) {
        Transport transport = queryOne(id);
        transport.setRegistered(TransportScheduleStatus.CANCELED.getType());
        updateOne(transport);
        ResponseEntity resp = dtxJobService.cancelJob(transport.getId());
        log.info(resp.toString());
        if (resp.getCode() != 200) {
            log.error("从调度中心注销任务失败,{}", resp.toString());
            throw HelperException.newException(TransportError.REGISTER_ERROR);
        }
    }
}

