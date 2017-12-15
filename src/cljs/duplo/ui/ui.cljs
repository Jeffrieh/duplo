(ns duplo.ui.ui
  (:require
   [rum.core :as rum]
   [duplo.ui.form :as form]))

(rum/defc block-item
  [{hsh :hash :keys [index confirmations size time tx] :as block}]
     [:div.row
     [:div.item.grow {:data-header "Validator"} [:span.hash 0 [:span.vert "x"] [:span.value (subs hsh 2)]]]
     [:div.item.fixed {:data-header "Transactions"} (count tx)]
     [:div.item.fixed {:data-header "Size"} size]
     [:div.item.fixed {:data-header "Confirmations"}confirmations]]
   )

(rum/defc block-list < rum/reactive
  [block-ids blocks]
  (conj
   [:div]
    [:div.table
   (map #(block-item (get (rum/react blocks) %))
        (reverse (rum/react block-ids)))]))

(rum/defc asset-item
  [{names :name :keys [type amount admin txid] :as asset}]
  (let [name-en (->> names (filter #(= (:lang %) "en")) first :name)]
    [:div.row
     [:div.item.fixed {:data-header "Title"} name-en]
     [:div.item.grow {:data-header "tx ID"} txid]
     [:div.item.fixed {:data-header "Type"} type]
     [:div.item.fixed {:data-header "Amount"} amount]
     [:div.item.fixed {:data-header "Admin"} admin]]))

(rum/defc asset-list < rum/reactive [items]
  (conj
   [:div]
    [:div.table
   (map asset-item (rum/react items))]))

(defn wallet-item
  [{:keys [public-key address wif]
    {:keys [neo gas]} :balance :as key}]
  [:div.row
   [:div.item.grow {:data-header "Address"} address]
   [:div.item.grow {:data-header "Public-key"} public-key]
   [:div.item.fixed {:data-header "NEO"} neo]
   [:div.item.fixed {:data-header "GAS"} gas]])

(defn wallet-menu [callback-fn]
  [:p
   [:button {:on-click #(callback-fn [:generate-keys])}
    "Generate Keys"]
   [:button {:on-click #(callback-fn [:claim-initial-neo])}
    "Claim Initial NEO"]
   [:button {:on-click #(callback-fn [:open-form :make-tx])}
    "Make Transaction"]])

(rum/defc wallet-list < rum/reactive
  [key-pairs callback-fn]
  (conj
   [:div.table
    (wallet-menu callback-fn)
    (form/active-form callback-fn)]
   (conj [:div]
         (->> key-pairs rum/react
              (sort-by #(get-in % [:balance :neo]))
              reverse
              (map wallet-item)))))

(rum/defc page-blocks [state]
  [:div.block-list
   [:h3 "Blocks"]
   (block-list (rum/cursor-in state [:block-ids])
               (rum/cursor-in state [:blocks]))])

(rum/defc page-assets [state]
  [:div.block-list
   [:h3 "Assets"]
   (asset-list (rum/cursor-in state [:assets]))])

(rum/defc page-wallet [state callback-fn]
  [:div.block-list
   [:h3 "Keys"]
   (wallet-list (rum/cursor-in state [:keys]) callback-fn)])

(rum/defc main-menu [route]
  [:menu
   [:ul
    [:li.logo [:a {:href "/"} [:img {:src "img/Logo.svg"}] "DUPLO" ]]
    [:li [:a {:href "blocks"} [:img {:src "img/Blocks.svg"}] "Blocks" ]]
    [:li [:a {:href "assets"} [:img {:src "img/Assets.svg"}] "Assets" ]]
    [:li [:a {:href "contracts"} [:img {:src "img/Contract.svg"}] "Contracts" ]]
    [:li [:a {:href "wallet"} [:img {:src "img/Wallet.svg"}] "Wallet" ]]]])

(rum/defc app < rum/reactive [state callback-fn]
  [:div.app
   [:header (main-menu)]
   (case (rum/react (rum/cursor-in state [:route]))
     :blocks (page-blocks state)
     :assets (page-assets state)
     :wallet (page-wallet state callback-fn))])
