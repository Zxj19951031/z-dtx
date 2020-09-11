package org.zipper.transport.controller;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.zipper.helper.web.response.ResponseEntity;
import org.zipper.transport.enums.DbInfo;
import org.zipper.transport.enums.DbType;
import org.zipper.transport.pojo.dto.DbDTO;
import org.zipper.transport.pojo.dto.DbDeleteParams;
import org.zipper.transport.pojo.dto.DbInfoParams;
import org.zipper.transport.pojo.dto.DbQueryParams;
import org.zipper.transport.pojo.entity.DataBase;
import org.zipper.transport.pojo.vo.DbVO;
import org.zipper.transport.service.DbService;

import java.util.List;

/**
 * @author zhuxj
 */
@Api(tags = "数据源管理")
@RestController
@RequestMapping(value = "db")
public class DbController {

    @Autowired
    private DbService dbService;

    @ApiOperation(value = "新增数据源")
    @PostMapping(value = "addOne")
    public ResponseEntity<Long> addOne(@RequestBody DbDTO db) {
        long id = dbService.addOne(db);
        return ResponseEntity.success(id);
    }

    @ApiOperation(value = "查看数据源列表")
    @GetMapping(value = "listPage")
    public ResponseEntity<PageInfo<DbVO>> listPage(@ApiParam(value = "查询条件-数据源名称") @RequestParam(required = false) String dbName,
                                                   @ApiParam(value = "查询条件-数据源类型") @RequestParam(required = false) Integer dbType,
                                                   @ApiParam(value = "查询条件-页码，小于0取所有") @RequestParam(defaultValue = "1") Integer pageNum,
                                                   @ApiParam(value = "查询条件-单页大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        DbQueryParams params = DbQueryParams.builder().dbName(dbName).dbType(dbType).build();
        PageInfo<DbVO> records = dbService.queryByParams(params, pageNum, pageSize);
        return ResponseEntity.success(records);
    }

    @ApiOperation(value = "查看数据源详情")
    @GetMapping(value = "find")
    public ResponseEntity<DataBase> findOne(@ApiParam(value = "数据源编号") @RequestParam Integer id,
                                            @ApiParam(value = "数据源类型") @RequestParam Integer dbType) {

        DataBase db = dbService.queryOne(id, dbType);
        return ResponseEntity.success(db);

    }

    @ApiOperation(value = "更新数据源详情")
    @PostMapping(value = "modify")
    public ResponseEntity<Boolean> updateOne(@RequestBody DbDTO db) {
        boolean isSuccess = dbService.updateOne(db);
        return ResponseEntity.success(isSuccess);
    }

    @ApiOperation(value = "批量删除数据源")
    @PostMapping(value = "deleteBatch")
    public ResponseEntity<Boolean> deleteBatch(@RequestBody DbDeleteParams params) {
        boolean isSuccess = dbService.deleteBatch(params);
        return ResponseEntity.success(isSuccess);
    }

    @ApiOperation(value = "测试数据源连接")
    @PostMapping(value = "connection/check")
    public ResponseEntity<Boolean> checkConnection(@RequestBody DbDTO db) {
        boolean isSuccess = dbService.checkConnection(db);
        return ResponseEntity.success(isSuccess);
    }

    @ApiOperation(value = "数据源明细查询，包括库、表、字段")
    @PostMapping(value = "{type}/{id}/{tag}/get")
    public ResponseEntity<List<String>> getDetail(@ApiParam(value = "数据源类型") @PathVariable int type,
                                                  @ApiParam(value = "数据源编号") @PathVariable int id,
                                                  @ApiParam(value = "标签类型") @PathVariable String tag,
                                                  @RequestBody DbInfoParams params) {
        DataBase dataBase = dbService.queryOne(id, type);
        List<String> result = dbService.getInfo(dataBase, DbType.get(type), DbInfo.get(tag), params);
        return ResponseEntity.success(result);
    }

}
