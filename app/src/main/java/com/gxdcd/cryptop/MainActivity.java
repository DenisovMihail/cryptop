package com.gxdcd.cryptop;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gxdcd.cryptop.api.ApiUtils;
import com.gxdcd.cryptop.api.cmc.CmcProvider;
import com.gxdcd.cryptop.utils.EndlessRecyclerViewScrollListener;

public class MainActivity extends AppCompatActivity {

    private Menu optionsMenu;
    private RecyclerView recycler;
    private CoinRecyclerAdapter adapter;
    private SwipeRefreshLayout swipeContainer;
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!ApiUtils.networkIsNotAvailable(this))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            String message = this.getResources().getString(R.string.NoNetwork);
            builder.setMessage(message);
            builder.setPositiveButton(this.getResources().getString(R.string.OK), null);
            builder.create().show();
            return;
        }
        setup();
        Toast.makeText(this, this.getResources().getString(
                R.string.InitializingCoinmarketcap), Toast.LENGTH_SHORT).show();
        CmcProvider.onQuoteSymbolChange(
                this.getApplicationContext(), s -> {
                    // выполнить обновление списка
                    startWithApiKey();
                });
        startWithApiKey();
    }

    private void startWithApiKey() {
        CmcProvider.onApiKeyReady(
                this.getApplicationContext(),
                api_key -> start(api_key),
                error -> {
                    // ключ API не настроен так как отсутствует, или возникла
                    // другая ошибка при работе с сохраненными настройками
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(error);
                    builder.setPositiveButton("OK", null);
                    builder.setOnDismissListener(dialog -> startActivity(
                        new Intent(MainActivity.this, SettingsActivity.class)));
                    builder.create().show();
                });
    }

    private void start(String api_key) {
        // запускаем задачу получения данных от АПИ сервера
        CmcProvider.StartLoading(
                api_key,
                0,
                // в случае возникновения ошибки её описание будет предоставлено в переменной error
                (String error) -> {
                    if (error != null) {
                        adapter.CmcProviderError();
                        // отображаем сообщение о возникшей ошибке
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        String message = "Ошибка при загрузке данных Coinmarketcap:\n" + error;
                        builder.setMessage(message);
                        builder.setPositiveButton("OK", null);
                        builder.create().show();
                    } else {
                        // запускаем обновление адаптера от полученных данных
                        adapter.UpdateFromCmcProvider();
                        // отображаем сообщение о завершении загрузки
                        Toast.makeText(MainActivity.this,
                                "Данные Coinmarketcap загружены", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setup() {
        adapter = new CoinRecyclerAdapter(getApplicationContext(), (view, index) -> {
            Intent intent = new Intent(MainActivity.this, CoinPageActivity.class);
            intent.putExtra("index", index);
            // должна быть добавлена в манифест <activity android:name=".CoinPageActivity"></activity>
            startActivity(intent);
        });
        recycler = findViewById(R.id.recycler);
        recycler.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(linearLayoutManager);

        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };

        // Adds the scroll listener to RecyclerView
        recycler.addOnScrollListener(scrollListener);

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync();
            }
        });
    }

    private void fetchTimelineAsync(/*int offset*/) {
        Toast.makeText(MainActivity.this,
                "Обновление данных", Toast.LENGTH_SHORT).show();
        startWithApiKey();
        swipeContainer.setRefreshing(false);
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    private void loadNextDataFromApi(int offset) {
        // Toast.makeText(MainActivity.this,
        //         "Нужно больше данных " + offset, Toast.LENGTH_SHORT).show();
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
    }

    // переопределение для реакции на события меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            // запускаем экран с настройками
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        } else if (item.isChecked())
            // по умолчанию, если галочка уже установлена
            return super.onOptionsItemSelected(item);

        // если item без галочки, значит её можно установить
        // и поменять режим фильтра видимости монет по типу
        // при этом проверяем какой именно пункт меню выбран
        if (id == R.id.action_show_allcoins) {
            item.setChecked(true);
            optionsMenu.findItem(R.id.action_show_stablecoins).setChecked(false);
            optionsMenu.findItem(R.id.action_show_not_stablecoins).setChecked(false);
            adapter.setCoinVisibility(CoinVisibility.ALL);
            ApiUtils.setPreferenceValue(
                    this.getApplicationContext(),"coin_visibility","ALL");
            return true;
        } else if (id == R.id.action_show_stablecoins) {
            item.setChecked(true);
            optionsMenu.findItem(R.id.action_show_allcoins).setChecked(false);
            optionsMenu.findItem(R.id.action_show_not_stablecoins).setChecked(false);
            adapter.setCoinVisibility(CoinVisibility.STABLECOINS);
            ApiUtils.setPreferenceValue(
                    this.getApplicationContext(),"coin_visibility","STABLECOINS");
            return true;
        } else if (id == R.id.action_show_not_stablecoins) {
            item.setChecked(true);
            optionsMenu.findItem(R.id.action_show_allcoins).setChecked(false);
            optionsMenu.findItem(R.id.action_show_stablecoins).setChecked(false);
            adapter.setCoinVisibility(CoinVisibility.NO_STABLECOINS);
            ApiUtils.setPreferenceValue(
                    this.getApplicationContext(),"coin_visibility","NO_STABLECOINS");
            return true;
        }

        // по умолчанию
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        optionsMenu = menu;

        getMenuInflater().inflate(R.menu.main_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(adapter);
        String coin_visibility = ApiUtils.getPreferenceValue(
                this.getApplicationContext(),"coin_visibility","ALL");

        switch (coin_visibility) {
            case "ALL":
                optionsMenu.findItem(R.id.action_show_allcoins).setChecked(true);
                optionsMenu.findItem(R.id.action_show_stablecoins).setChecked(false);
                optionsMenu.findItem(R.id.action_show_not_stablecoins).setChecked(false);
                adapter.setCoinVisibility(CoinVisibility.ALL);
                break;
            case "STABLECOINS":
                optionsMenu.findItem(R.id.action_show_allcoins).setChecked(false);
                optionsMenu.findItem(R.id.action_show_stablecoins).setChecked(true);
                optionsMenu.findItem(R.id.action_show_not_stablecoins).setChecked(false);
                adapter.setCoinVisibility(CoinVisibility.STABLECOINS);
                break;
            case "NO_STABLECOINS":
                optionsMenu.findItem(R.id.action_show_allcoins).setChecked(false);
                optionsMenu.findItem(R.id.action_show_stablecoins).setChecked(false);
                optionsMenu.findItem(R.id.action_show_not_stablecoins).setChecked(true);
                adapter.setCoinVisibility(CoinVisibility.NO_STABLECOINS);
                break;
        }

        return true;
    }
}
