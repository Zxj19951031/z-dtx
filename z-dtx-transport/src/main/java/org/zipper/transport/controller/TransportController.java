package org.zipper.transport.controller;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.zipper.helper.web.response.ResponseEntity;
import org.zipper.transport.pojo.dto.TransportQueryParams;
import org.zipper.transport.pojo.entity.Transport;
import org.zipper.transport.pojo.vo.TransportInstanceVO;
import org.zipper.transport.pojo.vo.TransportVO;
import org.zipper.transport.service.TransportInstanceService;
import org.zipper.transport.service.TransportService;

import java.util.List;

/**
 * @author zhuxj
 */
@Api(tags = "传输管理")
@RestController
@RequestMapping(value = "transport")
@Slf4j
public class TransportController {

    @Autowired
    private TransportService transportService;

    @Autowired
    private TransportInstanceService transportInstanceService;


    @ApiOperation(value = "新增传输任务")
    @PostMapping(value = "add")
    public ResponseEntity<String> addOne(@RequestBody Transport transport) {
        int id = transportService.addOne(transport);
        return ResponseEntity.success("新增成功");
    }

    @ApiOperation(value = "查看传输任务列表")
    @GetMapping(value = "page/list")
    public ResponseEntity<PageInfo<TransportVO>> listPage(@ApiParam(value = "查询条件-传输名称") @RequestParam(required = false) String name,
                                                          @ApiParam(value = "查询条件-页码，小于0取所有") @RequestParam(defaultValue = "1") Integer pageNum,
                                                          @ApiParam(value = "查询条件-单页大小") @RequestParam(defaultValue = "20") Integer pageSize) {

        TransportQueryParams params = TransportQueryParams.builder().name(name).build();
        PageInfo<TransportVO> vos = this.transportService.queryByParams(params, pageNum, pageSize);
        return ResponseEntity.success(vos);
    }

    @ApiOperation(value = "查看传输任务详情")
    @GetMapping(value = "find")
    public ResponseEntity<Transport> findOne(@ApiParam(value = "传输任务编号") @RequestParam Integer id) {

        Transport transport = this.transportService.queryOne(id);
        return ResponseEntity.success(transport);
    }

    @ApiOperation(value = "更新数据源详情")
    @PostMapping(value = "modify")
    public ResponseEntity<String> updateOne(@RequestBody Transport db) {
        boolean isSuccess = this.transportService.updateOne(db);
        return ResponseEntity.success("更新成功");
    }

    /**
     * 这个接口是给外部调度服务进行唤起任务的
     *
     * @param id 传输任务编号
     */
    @ApiOperation(value = "运行传输任务")
    @GetMapping(value = "run")
    public ResponseEntity<String> runJob(@RequestParam Integer id) {
        transportService.run(id);
        return ResponseEntity.success("任务将在稍后执行，请观察日志查看任务运行状态");
    }

    /**
     * 将任务注册至调度服务中心
     */
    @ApiOperation(value = "注册传输任务")
    @GetMapping(value = "register")
    public ResponseEntity<String> registerJob(@RequestParam Integer id) {
        transportService.registerJob(id);
        return ResponseEntity.success("启动任务成功，系统将按计划进行调度");
    }

    @ApiOperation(value = "注销传输任务")
    @GetMapping(value = "cancel")
    public ResponseEntity<String> cancelJob(@RequestParam Integer id) {
        transportService.cancelJob(id);
        return ResponseEntity.success("注销任务成功");
    }


    @ApiOperation(value = "查看实例列表")
    @GetMapping(value = "instance/page/list")
    public ResponseEntity<PageInfo<TransportInstanceVO>> instanceListPage(@ApiParam(value = "查询条件-页码，小于0取所有") @RequestParam(defaultValue = "1") Integer pageNum,
                                                                          @ApiParam(value = "查询条件-单页大小") @RequestParam(defaultValue = "20") Integer pageSize,
                                                                          @ApiParam(value = "传输任务编号") @RequestParam Integer id) {

        PageInfo<TransportInstanceVO> vos = this.transportInstanceService.queryByTransportId(id, pageNum, pageSize);
        return ResponseEntity.success(vos);
    }


    @ApiOperation(value = "实例日志")
    @GetMapping(value = "instance/log")
    public ResponseEntity<List<String>> instanceLog(@ApiParam(value = "查询条件-页码，小于0取所有") @RequestParam(defaultValue = "0") Integer pageNum,
                                                    @ApiParam(value = "查询条件-单页大小") @RequestParam(defaultValue = "20") Integer pageSize,
                                                    @ApiParam(value = "传输任务编号实例") @RequestParam Integer instanceId) {

        return ResponseEntity.success(this.transportInstanceService.queryLog(instanceId, pageNum, pageSize));
    }


}
