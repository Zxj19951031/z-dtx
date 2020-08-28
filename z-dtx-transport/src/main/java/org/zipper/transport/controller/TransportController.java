package org.zipper.transport.controller;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import org.zipper.helper.web.response.ResponseEntity;
import org.zipper.transport.pojo.dto.DbDTO;
import org.zipper.transport.pojo.dto.TransportQueryParams;
import org.zipper.transport.pojo.entity.DataBase;
import org.zipper.transport.pojo.entity.Transport;
import org.zipper.transport.pojo.vo.TransportVO;
import org.zipper.transport.service.TransportService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhuxj
 */
@Api(tags = "传输管理")
@RestController
@RequestMapping(value = "transport")
public class TransportController {

    @Resource
    private TransportService transportService;

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
        PageInfo<TransportVO> vos = this.transportService.queryByParams(params,pageNum,pageSize);
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

}
