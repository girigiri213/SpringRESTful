package com.girigiri.controller;

import com.girigiri.controller.utils.PoiUtil;
import com.girigiri.dao.models.Customer;
import com.girigiri.dao.models.Request;
import com.girigiri.dao.services.CustomerRepository;
import com.girigiri.dao.services.RequestRepository;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by JianGuo on 6/26/16.
 * Rest controller for getting a confirmation from {@link com.girigiri.dao.models.Customer}
 */
@RestController
@RequestMapping(value = "/api/confirm")
public class ConfirmController extends BaseController {
    private final CustomerRepository customerRepository;
    private final RequestRepository requestRepository;


    @Autowired
    ConfirmController(CustomerRepository customerRepository, RequestRepository requestRepository) {
        this.customerRepository = customerRepository;
        this.requestRepository = requestRepository;
    }



    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getAllConfirm() {
        return new ResponseEntity<>(null, HttpStatus.METHOD_NOT_ALLOWED);
    }


    // TODO: 6/26/16 regroup customer and device to a confirm POJO
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public void getConfirm(HttpServletResponse response, @PathVariable Long id) throws IOException {
        Request request = requestRepository.findOne(id);
        Customer customer = customerRepository.findOne(request.getCusId());
        HSSFWorkbook workbook = PoiUtil.saveConfirmExcel(request, customer);
        response.setContentType("application/vnd.ms-excel; charset=utf-8");
        response.setHeader("Content-Disposition",
                "attachment; filename=" + "客户确认单.xls");
        workbook.write(response.getOutputStream()); // Write workbook to response.
        workbook.close();
    }

}
