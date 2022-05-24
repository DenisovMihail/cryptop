package com.gxdcd.cryptop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gxdcd.cryptop.api.cmc.CmcItem;
import com.gxdcd.cryptop.api.cmc.CmcMeta;
import com.gxdcd.cryptop.api.cmc.CmcProvider;
import com.gxdcd.cryptop.api.cmc.CmcQuote;

import java.util.ArrayList;
import java.util.List;

// Пример работы с RecyclerView
// https://guides.codepath.com/android/Using-the-RecyclerView

class CoinRecyclerAdapter extends RecyclerView.Adapter<CoinRecyclerAdapter.ViewHolder> implements SearchView.OnQueryTextListener {

    private Context context;
    private List<CmcItem> items = new ArrayList<>();
    private ItemClickListener clickListener;
    private CoinVisibility coinVisibility = CoinVisibility.ALL;
    private String filter;

    public CoinRecyclerAdapter(Context context, ItemClickListener itemClickListener) {
        this.context = context;
        this.clickListener = itemClickListener;
    }

    // Обновление данных визуального списка.
    // Вызывается после получения данных от сервера
    public void CmcProviderError() {
        items.clear();
        notifyDataSetChanged();
    }

    // Обновление данных визуального списка.
    // Вызывается после получения данных от сервера
    void UpdateFromCmcProvider() {
        items = getFilteredItems();
        notifyDataSetChanged();
    }

    /**
     * Called when the user submits the query. This could be due to a key press on the
     * keyboard or due to pressing a submit button.
     * The listener can override the standard behavior by returning true
     * to indicate that it has handled the submit request. Otherwise return false to
     * let the SearchView handle the submission by launching any associated intent.
     *
     * @param query the query text that is to be submitted
     * @return true if the query has been handled by the listener, false to let the
     * SearchView perform the default action.
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /**
     * Called when the query text is changed by the user.
     *
     * @param query the new content of the query text field.
     * @return false if the SearchView should perform the default action of showing any
     * suggestions if available, true if the action was handled by the listener.
     */
    @Override
    public boolean onQueryTextChange(@NonNull String query) {
        this.filter = query.toLowerCase();
        this.items = getFilteredItems();
        notifyDataSetChanged();
        return true;
    }

    public void setCoinVisibility(CoinVisibility coinVisibility) {
        this.coinVisibility = coinVisibility;
        this.items = getFilteredItems();
        notifyDataSetChanged();
    }

    @NonNull
    private List<CmcItem> getFilteredItems() {
        if (CmcProvider.Latest.data == null)
            return this.items;
        final List<CmcItem> filtered = new ArrayList<>();
        for (CmcItem item : CmcProvider.Latest.data) {
            if (coinVisibility.willShow(item)) {
                if (this.filter == null || this.filter == "")
                    filtered.add(item);
                else {
                    final String symbol = item.symbol.toLowerCase();
                    final String name = item.name.toLowerCase();
                    if (symbol.contains(this.filter) || name.contains(this.filter)) {
                        filtered.add(item);
                    }
                }
            }
        }
        return filtered;
    }

    // Интерфейс для обработчика клика
    // Реализуется внешним кодом, передается в setClickListener
    public interface ItemClickListener {
        void onItemClick(View view, int index);
    }

    private CmcItem getItem(int position) {
        return this.items.get(position);
    }

    private CmcMeta getMeta(CmcItem item) {
        return CmcProvider.Metadata.data.get(item.id);
    }

    // Создаем вспомогательный класс ViewHolder


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.coin_item, parent, false);
        return new ViewHolder(view);
    }

    // Выполняет заполнение данных каждого ряда на основании
    // текущей позиции, отображаемой в скроллинге

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Получаем данные из списка по отображаемой позиции
        CmcItem item = this.getItem(position);
        CmcMeta meta = this.getMeta(item);
        CmcQuote quote = item.getQuote();

        // запоминаем id для восстановления
        holder.id = item.id;

        Glide.with(context)
                .load(meta.logo)
                .override(64, 64)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.logo);

        holder.name.setText(String.format("%d %s (%s) %s", item.cmc_rank, item.name, item.symbol, item.isStablecoin() ? "стейблкойн" : ""));
        holder.price.setText(String.format("Цена: %,f %s", quote.price, item.getQuoteSymbol()));
        holder.marketCap.setText(String.format("Капитализация: %,d %s", Math.round(quote.marketCap), item.getQuoteSymbol()));
        holder.volume24h.setText(String.format("Объём/24ч: %,d", Math.round(quote.volume24h)));
        holder.textView1h.setText(String.format("1ч: %.2f", quote.percentChange1h) + "%");
        holder.textView24h.setText(String.format("24ч: %.2f", quote.percentChange24h) + "%");
        holder.textView7d.setText(String.format("7д: %.2f", quote.percentChange7d) + "%");
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Декларируются все визуальные элементы,
        // используемые для непосредственного доступа
        // В дальнейшем, инициализируются в конструкторе
        TextView name;
        TextView price;
        TextView marketCap;
        TextView volume24h;
        TextView textView1h;
        TextView textView24h;
        TextView textView7d;
        ImageView logo;
        Integer id;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            logo = itemView.findViewById(R.id.logo);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            marketCap = itemView.findViewById(R.id.marketCap);
            volume24h = itemView.findViewById(R.id.volume24h);
            textView1h = itemView.findViewById(R.id.textView1h);
            textView24h = itemView.findViewById(R.id.textView24h);
            textView7d = itemView.findViewById(R.id.textView7d);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                for (int i = 0; i < CmcProvider.Latest.data.size(); i++) {
                    if (CmcProvider.Latest.data.get(i).id == this.id) {
                        clickListener.onItemClick(v, i);
                        return;
                    }
                }
            }
        }

    }
}
