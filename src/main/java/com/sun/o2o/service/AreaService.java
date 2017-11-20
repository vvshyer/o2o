package com.sun.o2o.service;

import com.sun.o2o.entity.Area;

import java.util.List;

public interface AreaService {
    String  AREALISTKEY = "arealist";
    List<Area> getAreaList();
}
