
(function (){
    angular.module('app.detail')
        .config(configRoute);

    function configRoute($routeProvider) {

        $routeProvider
            .when('/detail/:agent/:product', {
                templateUrl  :  'components/detail/detail.html',
                controller : 'detailController',
                controllerAs : 'vm'
            });

    }
})();