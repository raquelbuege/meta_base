(ns metabase.api.routes
  (:require [compojure.core :refer [context defroutes]]
            [compojure.route :as route]
            [metabase.api.activity :as activity]
            [metabase.api.alert :as alert]
            [metabase.api.automagic-dashboards :as magic]
            [metabase.api.card :as card]
            [metabase.api.collection :as collection]
            [metabase.api.dashboard :as dashboard]
            [metabase.api.database :as database]
            [metabase.api.dataset :as dataset]
            [metabase.api.email :as email]
            [metabase.api.embed :as embed]
            [metabase.api.field :as field]
            [metabase.api.geojson :as geojson]
            [metabase.api.ldap :as ldap]
            [metabase.api.login-history :as login-history]
            [metabase.api.metric :as metric]
            [metabase.api.native-query-snippet :as native-query-snippet]
            [metabase.api.notify :as notify]
            [metabase.api.page-views :as page-views]
            [metabase.api.permissions :as permissions]
            [metabase.api.premium-features :as premium-features]
            [metabase.api.preview-embed :as preview-embed]
            [metabase.api.public :as public]
            [metabase.api.pulse :as pulse]
            [metabase.api.revision :as revision]
            [metabase.api.routes.common :refer [+apikey +auth +generic-exceptions +message-only-exceptions]]
            [metabase.api.search :as search]
            [metabase.api.segment :as segment]
            [metabase.api.session :as session]
            [metabase.api.setting :as setting]
            [metabase.api.setup :as setup]
            [metabase.api.slack :as slack]
            [metabase.api.table :as table]
            [metabase.api.task :as task]
            [metabase.api.testing :as testing]
            [metabase.api.tiles :as tiles]
            [metabase.api.transform :as transform]
            [metabase.api.user :as user]
            [metabase.api.user-activity :as user-activity]
            [metabase.api.user-queries :as user-queries]
            [metabase.api.util :as util]
            [metabase.config :as config]
            [metabase.plugins.classloader :as classloader]
            [metabase.util :as u]
            [metabase.util.i18n :refer [deferred-tru]]))

(comment metabase.actions.api/keep-me
         metabase.activity-feed.api/keep-me
         metabase.api.api-key/keep-me
         metabase.api.cache/keep-me
         metabase.api.card/keep-me
         metabase.api.cards/keep-me
         metabase.api.collection/keep-me
         metabase.api.dashboard/keep-me
         metabase.api.database/keep-me
         metabase.api.dataset/keep-me
         metabase.api.embed/keep-me
         metabase.api.field/keep-me
         metabase.api.geojson/keep-me
         metabase.api.native-query-snippet/keep-me
         metabase.api.preview-embed/keep-me
         metabase.api.setting/keep-me
         metabase.api.slack/keep-me
         metabase.api.table/keep-me
         metabase.api.task/keep-me
         metabase.api.testing/keep-me
         metabase.api.user/keep-me
         metabase.api.util/keep-me
         metabase.bookmarks.api/keep-me
         metabase.cloud-migration.api/keep-me
         metabase.indexed-entities.api/keep-me
         metabase.login-history.api/keep-me
         metabase.model-persistence.api/keep-me
         metabase.permissions.api/keep-me
         metabase.public-sharing.api/keep-me
         metabase.revisions.api/keep-me
         metabase.segments.api/keep-me
         metabase.setup.api/keep-me
         metabase.tiles.api/keep-me
         metabase.user-key-value.api/keep-me)

(def ^:private ^{:arglists '([request respond raise])} pass-thru-handler
  "Always 'falls thru' to the next handler."
  (open-api/handler-with-open-api-spec
   (fn [_request respond _raise]
     (respond nil))
   ;; no OpenAPI spec for this handler.
   (fn [_prefix]
     nil)))

(defroutes ^{:doc "Ring routes for API endpoints."} routes
  ee-routes
  (context "/activity"             [] (+auth activity/routes))
  (context "/alert"                [] (+auth alert/routes))
  (context "/automagic-dashboards" [] (+auth magic/routes))
  (context "/card"                 [] (+auth card/routes))
  (context "/collection"           [] (+auth collection/routes))
  (context "/dashboard"            [] (+auth dashboard/routes))
  (context "/database"             [] (+auth database/routes))
  (context "/dataset"              [] (+auth dataset/routes))
  (context "/email"                [] (+auth email/routes))
  (context "/embed"                [] (+message-only-exceptions embed/routes))
  (context "/field"                [] (+auth field/routes))
  (context "/geojson"              [] geojson/routes)
  (context "/ldap"                 [] (+auth ldap/routes))
  (context "/login-history"        [] (+auth login-history/routes))
  (context "/premium-features"     [] (+auth premium-features/routes))
  (context "/metric"               [] (+auth metric/routes))
  (context "/native-query-snippet" [] (+auth native-query-snippet/routes))
  (context "/notify"               [] (+apikey notify/routes))
  (context "/page-views"           [] (+auth page-views/routes))
  (context "/permissions"          [] (+auth permissions/routes))
  (context "/preview_embed"        [] (+auth preview-embed/routes))
  (context "/public"               [] (+generic-exceptions public/routes))
  (context "/pulse"                [] (+auth pulse/routes))
  (context "/revision"             [] (+auth revision/routes))
  (context "/search"               [] (+auth search/routes))
  (context "/segment"              [] (+auth segment/routes))
  (context "/session"              [] session/routes)
  (context "/setting"              [] (+auth setting/routes))
  (context "/setup"                [] setup/routes)
  (context "/slack"                [] (+auth slack/routes))
  (context "/table"                [] (+auth table/routes))
  (context "/task"                 [] (+auth task/routes))
  (context "/testing"              [] (if (or (not config/is-prod?)
                                              (config/config-bool :mb-enable-test-endpoints))
                                        testing/routes
                                        (fn [_ respond _] (respond nil))))
  (context "/tiles"                [] (+auth tiles/routes))
  (context "/transform"            [] (+auth transform/routes))
  (context "/user"                 [] (+auth user/routes))
  (context "/user-activity"        [] (+auth user-activity/routes))
  (context "/user-queries"        [] (+auth user-queries/routes))
  (context "/util"                 [] util/routes)
  (route/not-found (constantly {:status 404, :body (deferred-tru "API endpoint does not exist.")})))
