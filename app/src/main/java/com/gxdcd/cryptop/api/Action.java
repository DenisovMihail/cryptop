package com.gxdcd.cryptop.api;

// Интерфейс используемый для функции обратного вызова
// в асинхронных задачах, вызываемой после завершения задачи
public interface Action<Param> {
    void execute(Param p);
}
