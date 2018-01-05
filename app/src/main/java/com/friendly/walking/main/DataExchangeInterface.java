package com.friendly.walking.main;

/**
 * Created by jungjiwon on 2017. 10. 23..
 */

public interface DataExchangeInterface {

    enum CommandType {
        READ_WALKING_TIME_LIST,

    };

    public void functionByCommand(String email, CommandType type);
}
