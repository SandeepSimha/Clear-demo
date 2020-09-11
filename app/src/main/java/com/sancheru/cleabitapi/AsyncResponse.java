package com.sancheru.cleabitapi;

import java.util.List;

public interface AsyncResponse {
    void processFinish(List<String> output);
}