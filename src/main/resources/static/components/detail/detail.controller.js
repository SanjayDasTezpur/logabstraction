
(function(){

    angular.module('app.detail')
        .controller('detailController', detailController);


    function detailController(ApiService, $routeParams, $uibModal, $location) {
        var vm = this;
        var agent;
        vm.openModal = openModal;
        vm.backToMain = backToMain;
        activate();

        function activate() {
            agent = $routeParams.agent.split(".",1);
            ApiService.getStat($routeParams.product, agent).then(successCall).catch(failureCall);
            vm.header = 'Log Summary of ' + $routeParams.product + ' in agent ' + agent;
            vm.description= 'This table displays number of error, info and warnings of ' + $routeParams.product +
              ' in agent ' + agent;
        }

        function successCall(response) {
            vm.stat = response.data;
        }

        function failureCall(error) {
            vm.error = error;
        }

        function openModal(type)
        {
            $uibModal.open({
                templateUrl: 'components/displayLog/logModal.html',
                controller: 'displayLogCtrl as vm',
                backdrop: 'static',
                size: 'lg',
                resolve: {
                    params: function () {
                        return {
                            logType : type,
                            agent : agent,
                            productName: $routeParams.product
                        };
                    }
                }
            });
        }

        function backToMain(){
        $location.path( "/" );
        }
    }
})();