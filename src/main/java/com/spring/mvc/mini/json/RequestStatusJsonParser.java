package com.spring.mvc.mini.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.mvc.mini.pojo.ObjectClass;
import com.spring.mvc.mini.pojo.RequestStatus;
import com.spring.mvc.mini.pojo.RequestStatusListType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
public class RequestStatusJsonParser {

    private static final Logger LOG = LoggerFactory.getLogger(RequestStatusJsonParser.class);

    @Value("${path.json}")
    private String jsonPath;

    public int getMaxIntClass() {

        List<RequestStatus> list = this.readStatus();

        List<ObjectClass> objectClasses = list.get(list.size() - 1).getObjectClassesType().getObjectClasses();

        return objectClasses.get(objectClasses.size() - 1).getIntclass();
    }

    public int getLatestmocrid() {

        List<RequestStatus> mrsList = this.readStatus();

        return mrsList.get(mrsList.size() - 1).getmocrid();
    }


    public void writeStatus(RequestStatusListType mrslt) {

        try {
            new ObjectMapper().writeValue(new File(jsonPath), mrslt);
        } catch (IOException e) {
            LOG.error(e.toString());
        }

    }

    public List<RequestStatus> readStatus() {

        RequestStatusListType type = null;

        try {
            type = new ObjectMapper().readValue(new File(jsonPath), RequestStatusListType.class);

            LOG.debug(type.toString());

        } catch (IOException e) {

            LOG.error(e.toString());
        }

        return type.getRequestStatuses();
    }


}
