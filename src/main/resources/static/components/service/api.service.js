
(function(){

    angular.module('app.services')
        .factory('ApiService', ApiService);


    function ApiService($http) {


        return {
            getClients : getClients,
            getStat : getStat,
            getLog : getLog
        };

        function getClients () {
            return $http.get("/clients");
        }

        function getStat (prod, host) {
            return $http.get("/trend/" + host + "/" + prod + "/");
        }

        function getLog(prod, host, type) {
        return $http.get("/event/" + host + "/" + prod + "/" + type);
        }
    }

})();