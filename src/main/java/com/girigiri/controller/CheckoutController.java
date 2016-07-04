package com.girigiri.controller;

import com.girigiri.controller.utils.PoiUtil;
import com.girigiri.dao.models.ComponentRequest;
import com.girigiri.dao.models.Customer;
import com.girigiri.dao.models.Request;
import com.girigiri.dao.services.ComponentRequestRepository;
import com.girigiri.dao.services.CustomerRepository;
import com.girigiri.dao.services.RequestRepository;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by JianGuo on 7/4/16.
 * Controller for checkout
 */
@Controller
@RequestMapping(value = "/api/checkout")
public class CheckoutController {

    private final RequestRepository requestRepository;
    private final CustomerRepository customerRepository;
    private final ComponentRequestRepository componentRequestRepository;


    @Autowired
    public CheckoutController(RequestRepository requestRepository, CustomerRepository customerRepository, ComponentRequestRepository componentRequestRepository) {
        this.requestRepository = requestRepository;
        this.customerRepository = customerRepository;
        this.componentRequestRepository = componentRequestRepository;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public void getCheckout(HttpServletResponse response, @PathVariable Long id) throws IOException {
        Request request = requestRepository.findOne(id);
        Customer customer = customerRepository.findOne(request.getCusId());
        List<ComponentRequest> list = componentRequestRepository.findByHistory(request.getRepairHistory().getId());
        HSSFWorkbook workbook = PoiUtil.saveCheckoutBill(request, customer, list);
        response.setContentType("application/vnd.ms-excel; charset=utf-8");
        response.setHeader("Content-Disposition",
                "attachment; filename=" + "checkout.xls");
        workbook.write(response.getOutputStream()); // Write workbook to response.
        workbook.close();
    }

}
