package net.lemonplan.dao;

import net.lemonplan.pojo.Request;

import java.util.List;

public interface RequestMapper {
    int insert(Request request);

    Request queryRequest(Request request);

    List<String> queryByAcceptUserId(String userId);

    int deleteRequest(Request request);
}
