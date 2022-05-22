package com.gxdcd.cryptop;

import com.gxdcd.cryptop.api.cmc.CmcItem;

enum CoinVisibility {
    ALL,
    STABLECOINS,
    NO_STABLECOINS;

    public boolean willShow(CmcItem item) {
        switch (this) {
            case STABLECOINS:
                return item.isStablecoin();
            case NO_STABLECOINS:
                return !item.isStablecoin();
        }
        return true;
    }
}
