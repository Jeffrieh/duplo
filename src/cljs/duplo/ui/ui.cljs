(ns duplo.ui.ui
  (:require
   [rum.core :as rum]))

(rum/defc block-item
  [{hsh :hash :keys [index confirmations size time tx] :as block}]
  [:div.block
   [:span.title index]
   [:span.hash hsh]
   [:dl.attrs
    [:dt "transactions:"] [:dd (count tx)]
    [:dt "size:"] [:dd size]
    [:dt "confirmations:"] [:dd confirmations]]])

(rum/defc block-list < rum/reactive
  [block-ids blocks]
  (conj
   [:div]
   (map #(block-item (get (rum/react blocks) %))
        (reverse (rum/react block-ids)))))

(rum/defc asset-item
  [{names :name :keys [type amount admin txid] :as asset}]
  (let [name-en (->> names (filter #(= (:lang %) "en")) first :name)]
    [:div.block
     [:span.title name-en]
     [:span.tx-id txid]
     [:dl.attrs
      [:dt "type:"] [:dd type]
      [:dt "amount:"] [:dd amount]
      [:dt "admin:"] [:dd admin]]]))

(rum/defc asset-list < rum/reactive [items]
  (conj
   [:div]
   (map asset-item (rum/react items))))

(rum/defc page-blocks [state]
  [:div.block-list
   [:h3 "Blocks"]
   (block-list (rum/cursor-in state [:block-ids])
               (rum/cursor-in state [:blocks]))])

(rum/defc page-assets [state]
  [:div.block-list
   [:h3 "Assets"]
   (asset-list (rum/cursor-in state [:assets]))])

(rum/defc main-menu []
  [:menu
   [:ul
    [:li [:a {:href "blocks"} "Blocks"]]
    [:li [:a {:href "assets"} "Assets"]]
    [:li [:a {:href "wallet"} "Wallet"]]]])

(rum/defc app < rum/reactive [state callback-fn]
  [:div.app
   [:header (main-menu)]
   (case (rum/react (rum/cursor-in state [:route]))
     :blocks (page-blocks state)
     :assets (page-assets state))])
