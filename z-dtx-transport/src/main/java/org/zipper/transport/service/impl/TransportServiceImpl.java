package org.zipper.transport.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.zipper.transport.enums.DbType;
import org.zipper.transport.mapper.TransportMapper;
import org.zipper.transport.pojo.dto.TransportQueryParams;
import org.zipper.transport.pojo.entity.DataBase;
import org.zipper.transport.pojo.entity.MySqlDb;
import org.zipper.transport.pojo.entity.Transport;
import org.zipper.transport.pojo.vo.TransportVO;
import org.zipper.transport.service.DbService;
import org.zipper.transport.service.TransportService;
import org.zipper.transport.utils.ConfigUtil;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhuxj
 * @since 2020/08/27
 */
@Service
public class TransportServiceImpl implements TransportService {

    @Resource
    private TransportMapper transportMapper;
    @Resource
    private DbService dbService;

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
}
