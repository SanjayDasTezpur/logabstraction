
(function(){

    angular.module('app.home')
        .controller('homeController', homeController);


    function homeController(ApiService) {
        var vm = this;
        vm.busyMessage = 'Please wait ...';
        vm.showSplash = false;
        activate();

        function activate() {
            ApiService.getClients().then(successCall).catch(failureCall)
        }

        function successCall(response) {
            vm.clients = response.data;
            vm.showSplash = true;

        }

        function failureCall(error) {
            vm.error = error;
            vm.showSplash = true;
        }
    }

})();