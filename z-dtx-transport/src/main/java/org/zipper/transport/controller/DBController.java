package org.zipper.transport.controller;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.zipper.common.response.RespResult;
import org.zipper.transport.pojo.dto.DBDeleteParams;
import org.zipper.transport.pojo.dto.DBQueryParams;
import org.zipper.transport.pojo.entity.DataBase;
import org.zipper.transport.pojo.vo.DBVO;
import org.zipper.transport.service.DBService;
import org.zipper.transport.pojo.dto.DBDTO;

@RestController
@RequestMapping(value = "db")
public class DBController {

    @Autowired
    private DBService dbService;


    @PostMapping(value = "addOne")
    public RespResult<Integer> addOne(@RequestBody DBDTO db) {
        int id = dbService.addOne(db);
        return RespResult.success(id);
    }

    @GetMapping(value = "listPage")
    public RespResult<PageInfo<DBVO>> listPage(@RequestParam(required = false) String dbName,
                                               @RequestParam(required = false) Integer dbType,
                                               @RequestParam(defaultValue = "0") Integer pageNum,
                                               @RequestParam(defaultValue = "20") Integer pageSize) {
        DBQueryParams params = DBQueryParams.builder().dbName(dbName).dbType(dbType).build();
        PageInfo<DBVO> records = dbService.queryByParams(params, pageNum, pageSize);
        return RespResult.success(records);
    }

    @GetMapping(value = "find")
    public RespResult<DataBase> findOne(@RequestParam Integer id, @RequestParam Integer dbType) {

        DataBase db = dbService.queryOne(id, dbType);
        return RespResult.success(db);

    }

    @PostMapping(value = "modify")
    public RespResult<Boolean> updateOne(@RequestBody DBDTO db) {
        boolean isSuccess = dbService.updateOne(db);
        return RespResult.success(isSuccess);
    }

    @PostMapping(value = "deleteBatch")
    public RespResult<Boolean> deleteBatch(@RequestBody DBDeleteParams params) {
        boolean isSuccess = dbService.deleteBatch(params);
        return RespResult.success(isSuccess);
    }

}
