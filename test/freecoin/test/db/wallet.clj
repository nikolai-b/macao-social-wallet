;; Freecoin - digital social currency toolkit

;; part of Decentralized Citizen Engagement Technologies (D-CENT)
;; R&D funded by the European Commission (FP7/CAPS 610349)

;; Copyright (C) 2015 Dyne.org foundation
;; Copyright (C) 2015 Thoughtworks, Inc.

;; Sourcecode designed, written and maintained by
;; Denis Roio <jaromil@dyne.org>

;; With contributions by
;; Duncan Mortimer <dmortime@thoughtworks.com>

;; This program is free software: you can redistribute it and/or modify
;; it under the terms of the GNU Affero General Public License as published by
;; the Free Software Foundation, either version 3 of the License, or
;; (at your option) any later version.

;; This program is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; GNU Affero General Public License for more details.

;; You should have received a copy of the GNU Affero General Public License
;; along with this program.  If not, see <http://www.gnu.org/licenses/>.

(ns freecoin.test.db.wallet
  (:require [midje.sweet :refer :all]
            [freecoin.db.uuid :as uuid]
            [freecoin.db.mongo :as fm]
            [freecoin.blockchain :as fb]
            [freecoin.db.wallet :as wallet]))

(facts "Can create and fetch an empty wallet"
       (against-background (uuid/uuid) => "a-uuid")
       (let [wallet-store (fm/create-memory-store)]
         (fact "can create a wallet"
               (wallet/new-empty-wallet! wallet-store
                                         "sso-id" "name" "test@email.com")
               => (just {:uid "a-uuid"
                         :sso-id "sso-id"
                         :participant {:name "name" :email "test@email.com"}
                         :public-key nil
                         :private-key nil
                         :blockchains {}
                         :blockchain-secrets {}}))
         (fact "can fetch the wallet by its uid"
               (wallet/fetch wallet-store "a-uuid")
               => (just {:uid "a-uuid"
                         :sso-id "sso-id"
                         :participant {:name "name" :email "test@email.com"}
                         :public-key nil
                         :private-key nil
                         :blockchains {}
                         :blockchain-secrets {}}))
         
         (fact "can fetch wallet by sso-id"
               (wallet/fetch-by-sso-id wallet-store "sso-id")
               => (just {:uid "a-uuid"
                         :sso-id "sso-id"
                         :participant {:name "name" :email "test@email.com"}
                         :public-key nil
                         :private-key nil
                         :blockchains {}
                         :blockchain-secrets {}}))))

(fact "Can add a new blockchain to an existing wallet"
      (let [wallet-store (fm/create-memory-store)
            blockchain (fb/create-in-memory-blockchain :bk)
            wallet (wallet/new-empty-wallet! wallet-store "sso-id" "name" "test@email.com")
            updated-wallet (wallet/add-blockchain-to-wallet-with-id! wallet-store
                                                                     blockchain
                                                                     (:uid wallet))]
        (:blockchains updated-wallet) => (contains {:bk anything})
        (:blockchain-secrets updated-wallet) => (contains {:bk anything})))