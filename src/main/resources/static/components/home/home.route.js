
(function (){
    angular.module('app.home')
        .config(configRoute);

    function configRoute($routeProvider) {

        $routeProvider
            .when('/', {
                templateUrl  :  'components/home/home.html',
                controller : 'homeController',
                controllerAs : 'vm'
            });

    }
})();