package com.gxdcd.cryptop.api.cmc;

// Каждый вызов API coinmarketcap содержит
// статус вызова и результирующие данные:
// Пример данных, получаемых от API
/*
    {
        data": [
            {
                ...
            }
        ],
        "status":
        {
            "timestamp": "2022-05-15T07:43:41.418Z",
            "error_code": 0,
            "error_message": null,
            "elapsed": 25,
            "credit_count": 1,
            "notice": null,
            "total_count": 10105
        }
    }
*/

// Класс CmcStatus предназначен для
// отображения статуса API в java-класс и получения
// информации об ошибке в случае её возникновения
public class CmcStatus {
    // Используем только необходимые для работы данные
    public String error_message;
}