package com.gxdcd.cryptop;

import com.gxdcd.cryptop.api.cmc.CmcItem;

enum CoinVisibility {
    ALL,
    STABLECOINS,
    NO_STABLECOINS,
    FAVORITES;

    public boolean willShow(CmcItem item, IsFavorite isFavorite) {
        switch (this) {
            case STABLECOINS:
                return item.isStablecoin();
            case NO_STABLECOINS:
                return !item.isStablecoin();
            case FAVORITES:
                return isFavorite.getIsFavorite(item.id);
        }
        return true;
    }

    public interface IsFavorite {
        boolean getIsFavorite(Integer id);
    }
}
