package com.miko.s4netty.controller;

import com.miko.s4netty.dto.CarDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/*
 * The MIT License
 *
 * Copyright 2014 Miroslav Kopecky.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/**
 * @author miroslavkopecky
 *
 * basic controller
 *
 */

@Controller
@RequestMapping("/")
public class MainController {

    private static final String S4_NETTY_WORKER_LINK = "http://localhost:10022";
    private static final String MESSAGE = "Spring 4 Netty is Here! JRebel";

    private final Logger logger = LoggerFactory.getLogger(MainController.class);

    RestTemplate restTemplate = new RestTemplate();

    /**
     * provide the main web result
     * @param model
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public String printWelcome(ModelMap model) {

        logger.debug("printWelcome Message = " + MESSAGE);

        model.addAttribute("message", MESSAGE);
        model.addAttribute("displayCars", getSimpleCarList());

        return "main";
    }

    /**
     * get the information from the s4netty-worker JSON service
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<CarDTO> getSimpleCarList(){
        List<CarDTO> result = new ArrayList<>();

        try{
            result = restTemplate.getForObject(S4_NETTY_WORKER_LINK, List.class);
            logger.debug("getSimpleCarList result= " + result);
        }catch (HttpClientErrorException e){
            logger.error("getSimpleCarList jsonCarList e= " + e);
        }

        return result;
    }

}
