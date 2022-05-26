package com.gxdcd.cryptop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.gxdcd.cryptop.api.binance.BinCandle;
import com.gxdcd.cryptop.api.binance.BinCandles;
import com.gxdcd.cryptop.api.binance.BinInterval;
import com.gxdcd.cryptop.api.binance.BinParams;
import com.gxdcd.cryptop.api.binance.BinanceProvider;
import com.gxdcd.cryptop.api.cmc.CmcItem;
import com.gxdcd.cryptop.api.cmc.CmcMeta;
import com.gxdcd.cryptop.api.cmc.CmcProvider;
import com.gxdcd.cryptop.api.cmc.CmcQuote;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CoinPageActivity extends AppCompatActivity {

    private boolean night;

    void setText(@IdRes int id, String format, Object... args) {
        TextView view = findViewById(id);
        view.setText(String.format(format, args));
    }

    void setText(@IdRes int id, String text) {
        TextView view = findViewById(id);
        view.setText(text);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coin_page);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        CmcItem item = CmcProvider.Latest.data.get(intent.getIntExtra("index", 0));
        CmcMeta meta = CmcProvider.Metadata.data.get(item.id);
        CmcQuote quote = item.getQuote();

        ImageView logo = findViewById(R.id.logo);
        if (meta != null)
            Glide.with(this.getApplicationContext())
                    .load(meta.logo)
                    .override(64, 64)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(logo);

        setText(R.id.name, item.name + " (" + item.symbol + ")");
        setText(R.id.price, "Цена: %,f %s", quote.price, item.getQuoteSymbol());
        setText(R.id.volume24h, "Объем (24 ч) %,d %s", Math.round(quote.volume24h), item.getQuoteSymbol());
        setText(R.id.market_cap, "Капитализация: %,d %s", Math.round(quote.marketCap), item.getQuoteSymbol());
        setText(R.id.circulating_supply, "Предложение: %,d %s", Math.round(item.circulating_supply), item.symbol);
        setText(R.id.max_supply,
                "Макс. предложение: %s", item.max_supply == null ? "∞" : String.format("%,d %s", Math.round(item.max_supply), item.symbol));

        setText(R.id.change1h, String.format("1ч: %.2f", quote.percentChange1h) + "%");
        setText(R.id.change24h, String.format("24ч: %.2f", quote.percentChange24h) + "%");
        setText(R.id.change7d, String.format("7д: %.2f", quote.percentChange7d) + "%");
        setText(R.id.tags, "Тэги: " + TextUtils.join(", ", item.tags));

        // получаем настройки темы и определяем какой цвет использовать

        int nightMode = this.getApplicationContext()
                .getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightMode == Configuration.UI_MODE_NIGHT_YES) {
            CONTRAST_COLOR = Color.WHITE;
            BACKGROUND_COLOR = Color.BLACK;
            this.night = true;
        } else {
            this.night = false;
        }

        // получаем настройки графика и определяем какой тип графика использовать
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        String chart_type = sharedPreferences.getString("chart_type", "");
        String chart_tf = sharedPreferences.getString("chart_tf", "4h");
        Integer chart_bar_count = sharedPreferences.getInt("chart_bar_count", DEFAULT_BINANCE_CANDLES_COUNT_LIM);
        boolean useLineChart = chart_type == null || chart_type.equals("line");

        String pair = item.symbol + "USDT";
        BinInterval interval = BinInterval.from(chart_tf);

        SetupLineChart(pair, interval, useLineChart);
        SetupCandleChart(pair, interval, !useLineChart);

        if (item.symbol.equals("USDT")){
            Toast.makeText(this,
                    "График для данной пары отсутствует", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this,
                String.format("Загружаем данные %s%s", item.symbol, "USDT"), Toast.LENGTH_SHORT).show();
        BinanceProvider.Load(
                new BinParams(pair, interval, chart_bar_count),
                (BinCandles candles) -> {
                    if (candles.data.length > 0) {
                        Toast.makeText(this,
                                String.format("Данные %s%s загружены", item.symbol, "USDT"), Toast.LENGTH_SHORT).show();
                        FillLineChart(candles, pair, interval, useLineChart);
                        FillCandleChart(candles, pair, interval, !useLineChart, this.night);
                    } else {
                        Toast.makeText(this,
                                String.format("График для пары %s%s отсутствует",
                                        item.symbol, "USDT"), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @ColorInt
    int CONTRAST_COLOR = Color.BLACK;
    @ColorInt
    int BACKGROUND_COLOR = Color.WHITE;
    private LineChart lineChart;
    private CandleStickChart candleChart;

    void SetupLineChart(String pair, BinInterval interval, boolean visible) {

        lineChart = findViewById(R.id.lineChart);
        if (!visible) {
            lineChart.setVisibility(View.GONE);
            return;
        }
        lineChart.setVisibility(View.VISIBLE);

        // стиль графика
        {
            lineChart.setBackgroundColor(BACKGROUND_COLOR);
            lineChart.setDrawGridBackground(false);
            // отключаем текст легенды и включаем описание на графике
            lineChart.getLegend().setEnabled(false);
            lineChart.getDescription().setEnabled(true);
            lineChart.getDescription().setText(pair + ":" + interval.translate());

            // масштабирование и перетаскивание
            // отдельно по x- и y- осям
            lineChart.setPinchZoom(false);
        }

        // стиль осей
        {
            // убираем правую вертикальную шкалу
            lineChart.getAxisRight().setEnabled(false);
            // левая вертикальная шкала внутри
            lineChart.getAxisLeft().setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);

            // настраиваем XY
            {
                XAxis xAxis = lineChart.getXAxis();
                YAxis yAxis = lineChart.getAxisLeft();
                // форматирование даты
                xAxis.setValueFormatter(new XAxisValueFormatter(interval));
                // метки внизу
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

                // горизонтальные и вертикальные линии графика
                xAxis.enableGridDashedLine(10f, 10f, 0f);
                yAxis.enableGridDashedLine(10f, 10f, 0f);
            }
        }

        /*
        {   // // Create Limit Lines // //
            LimitLine llXAxis = new LimitLine(9f, "Index 10");
            llXAxis.setLineWidth(4f);
            llXAxis.enableDashedLine(10f, 10f, 0f);
            llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            llXAxis.setTextSize(10f);
            // llXAxis.setTypeface(tfRegular);

            LimitLine ll1 = new LimitLine(150f, "Upper Limit");
            ll1.setLineWidth(4f);
            ll1.enableDashedLine(10f, 10f, 0f);
            ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            ll1.setTextSize(10f);
            // ll1.setTypeface(tfRegular);

            LimitLine ll2 = new LimitLine(-30f, "Lower Limit");
            ll2.setLineWidth(4f);
            ll2.enableDashedLine(10f, 10f, 0f);
            ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            ll2.setTextSize(10f);
            // ll2.setTypeface(tfRegular);

            // draw limit lines behind data instead of on top
            yAxis.setDrawLimitLinesBehindData(true);
            xAxis.setDrawLimitLinesBehindData(true);

            // add limit lines
            yAxis.addLimitLine(ll1);
            yAxis.addLimitLine(ll2);
            //xAxis.addLimitLine(llXAxis);
        }
        */
    }

    void SetupCandleChart(String pair, BinInterval interval, boolean visible) {

        candleChart = findViewById(R.id.candleChart);
        if (!visible) {
            candleChart.setVisibility(View.GONE);
            return;
        }
        candleChart.setVisibility(View.VISIBLE);

        // стиль графика
        {
            candleChart.setBackgroundColor(BACKGROUND_COLOR);
            candleChart.setDrawGridBackground(false);
            // отключаем текст легенды и включаем описание на графике
            candleChart.getLegend().setEnabled(false);
            candleChart.getDescription().setEnabled(true);
            candleChart.getDescription().setText(pair + ":" + interval.translate());

            // if more than 60 entries are displayed in the chart, no values will be
            // drawn
            candleChart.setMaxVisibleValueCount(60);

            // масштабирование и перетаскивание
            // отдельно по x- и y- осям
            candleChart.setPinchZoom(false);
        }

        // стиль осей
        {
            // убираем правую вертикальную шкалу
            candleChart.getAxisRight().setEnabled(false);
            // левая вертикальная шкала внутри
            candleChart.getAxisLeft().setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);

            // настраиваем XY
            {
                XAxis xAxis = candleChart.getXAxis();
                YAxis yAxis = candleChart.getAxisLeft();
                // форматирование даты
                xAxis.setValueFormatter(new XAxisValueFormatter(interval));
                // метки внизу
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

                // горизонтальные и вертикальные линии графика
                xAxis.enableGridDashedLine(10f, 10f, 0f);
                yAxis.enableGridDashedLine(10f, 10f, 0f);

                //xAxis.setDrawGridLines(false);
                //yAxis.setLabelCount(7, false);
                //yAxis.setDrawGridLines(false);
                //yAxis.setDrawAxisLine(false);
            }

        }
    }

    void FillLineChart(@NonNull BinCandles candles, String pair, BinInterval interval, boolean visible) {

        if (!visible) {
            return;
        }

        ArrayList<Entry> values = new ArrayList<>();

        for (int i = 0; i < candles.data.length; i++) {
            BinCandle candle = candles.data[i];
            values.add(new Entry(
                    (float) candle.openTime / CoinPageActivity.GetFixFor(interval),
                    (float) candle.close));
        }

        LineDataSet set1 = new LineDataSet(values, pair);

        // методы BaseDataSet
        {
            set1.setDrawIcons(false);
            set1.setDrawValues(false);
            // black lines
            set1.setColor(CONTRAST_COLOR);
            // customize legend entry
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);
            // text size of values
            set1.setValueTextSize(9f);
        }

        // методы производных BaseDataSet
        {
            // draw dashed line
            set1.enableDashedLine(10f, 5f, 0f);
            // black points
            set1.setCircleColor(CONTRAST_COLOR);
            // line thickness and point size
            set1.setLineWidth(2f);
            set1.setCircleRadius(3f);
            // draw points as solid circles
            set1.setDrawCircleHole(false);
            // draw selection line as dashed
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            // set the filled area
            set1.setDrawFilled(true);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return lineChart.getAxisLeft().getAxisMinimum();
                }
            });
            set1.setFillColor(CONTRAST_COLOR);
        }

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the data sets

        // create a data object with the data sets
        LineData data = new LineData(dataSets);

        // set data
        lineChart.setData(data);
        lineChart.animateX(50);
    }

    void FillCandleChart(@NonNull BinCandles candles, String pair, BinInterval interval, boolean visible, boolean night) {
        if (!visible) {
            return;
        }

        ArrayList<CandleEntry> values = new ArrayList<>();

        for (int i = 0; i < candles.data.length; i++) {
            BinCandle candle = candles.data[i];
            values.add(new CandleEntry(
                    (float) candle.openTime / CoinPageActivity.GetFixFor(interval),
                    (float) candle.high,
                    (float) candle.low,
                    (float) candle.open,
                    (float) candle.close));
        }

        CandleDataSet set1 = new CandleDataSet(values, pair);

        // методы BaseDataSet
        {
            set1.setDrawIcons(false);
            set1.setDrawValues(false);
            // black lines
            set1.setColor(CONTRAST_COLOR);
            // customize legend entry
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);
            // text size of values
            set1.setValueTextSize(9f);
        }

        // методы производных BaseDataSet
        {
            set1.setHighlightLineWidth(1f);
            set1.setShowCandleBar(true);
            set1.setShadowWidth(1f);

            set1.setShadowColor(CONTRAST_COLOR);
            set1.setDecreasingColor(CONTRAST_COLOR);
            set1.setIncreasingColor(CONTRAST_COLOR);
            if (night) {
                set1.setDecreasingPaintStyle(Paint.Style.STROKE);
                set1.setIncreasingPaintStyle(Paint.Style.FILL);
            } else {
                set1.setDecreasingPaintStyle(Paint.Style.FILL);
                set1.setIncreasingPaintStyle(Paint.Style.STROKE);
            }
            set1.setNeutralColor(CONTRAST_COLOR);

            // draw selection line as dashed
            set1.enableDashedHighlightLine(10f, 5f, 0f);
        }

        CandleData data = new CandleData(set1);

        // set data
        candleChart.setData(data);
        candleChart.animateX(50);
    }

    // https://stackoverflow.com/questions/53128197/mpandroidchart-combinedchart-candlestick-real-body-not-showing
    static final Integer DEFAULT_BINANCE_CANDLES_COUNT_LIM = 30;

    static float GetFixFor(BinInterval interval) {
        return interval.getSeconds() * 1075f;
    }

}

// обеспечиваем форматирование метки оси X в виде даты
class XAxisValueFormatter extends ValueFormatter {
    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM");
    BinInterval interval;

    public XAxisValueFormatter(BinInterval interval) {
        this.interval = interval;
        if (interval.getSeconds() < BinInterval.HOURLY.getSeconds()) {
            // часы:минуты
            sdf = new SimpleDateFormat("hh:mm");
        } else if (interval.getSeconds() < BinInterval.DAILY.getSeconds()) {
            // часы:минуты
            sdf = new SimpleDateFormat("hh:mm");
        } else {
            // дни.месяцы
            sdf = new SimpleDateFormat("dd.MM");
        }
    }

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        Date d = new Date(Float.valueOf(
                value * CoinPageActivity.GetFixFor(interval)
        ).longValue());
        return sdf.format(d);
    }
}


