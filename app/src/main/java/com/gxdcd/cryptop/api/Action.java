package com.gxdcd.cryptop.api;

import android.graphics.Color;

// Интерфейс используемый для функции обратного вызова
// в асинхронных задачах, вызываемой после завершения задачи
// (functional interface)
public interface Action<Param> {
    void execute(Param p); // Action Of Param
}