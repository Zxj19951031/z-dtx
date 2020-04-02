package org.zipper.db.controller;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.zipper.common.response.RespResult;
import org.zipper.db.pojo.dto.DBDTO;
import org.zipper.db.pojo.dto.DBDeleteParams;
import org.zipper.db.pojo.dto.DBQueryParams;
import org.zipper.db.pojo.vo.DBVO;
import org.zipper.db.service.DBService;

@RestController
@RequestMapping(value = "db")
public class DBController {

    @Autowired
    private DBService dbService;

    @PostMapping(value = "addOne")
    public RespResult addOne(@RequestBody DBDTO db) {
        int id = dbService.addOne(db);
        return RespResult.success(id);
    }

    @GetMapping(value = "listPage")
    public RespResult listPage(@RequestParam(required = false) String dbName,
                               @RequestParam(required = false) Integer dbType,
                               @RequestParam(defaultValue = "0") Integer pageNum,
                               @RequestParam(defaultValue = "20") Integer pageSize) {
        DBQueryParams params = DBQueryParams.builder().dbName(dbName).dbType(dbType).build();
        PageInfo<DBVO> records = dbService.queryByParams(params, pageNum, pageSize);
        return RespResult.success(records);
    }

    @PostMapping(value = "modify")
    public RespResult updateOne(@RequestBody DBDTO db) {
        boolean isSuccess = dbService.updateOne(db);
        return RespResult.success(isSuccess);
    }

    @PostMapping(value = "deleteBatch")
    public RespResult deleteBatch(@RequestBody DBDeleteParams params) {
        boolean isSuccess = dbService.deleteBatch(params);
        return RespResult.success(isSuccess);
    }

    @PostMapping(value = "testConnective")
    public RespResult testConnective(@RequestBody DBDTO db) {
        // TODO: 2020-04-02 rpc
        return RespResult.success(null);
    }
}
