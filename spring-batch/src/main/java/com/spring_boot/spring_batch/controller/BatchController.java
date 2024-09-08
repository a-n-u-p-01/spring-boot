package com.spring_boot.spring_batch.controller;

import com.spring_boot.spring_batch.entity.Order;
import com.spring_boot.spring_batch.repository.OrderRepo;
import com.spring_boot.spring_batch.service.BatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("batch-write")
public class BatchController {

    @Autowired
    private BatchService batchService;

    @Autowired
    private OrderRepo orderRepo;

    @GetMapping
    public ResponseEntity startProcess(){
        try {
            batchService.startBatch();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping
    public ResponseEntity putOrder(@RequestBody Order order){
        orderRepo.save(order);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
