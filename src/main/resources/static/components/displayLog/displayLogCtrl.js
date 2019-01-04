(function () {
    'use strict';

    angular
        .module('app.displayLog')
        .controller('displayLogCtrl', displayLogCtrl);

    function displayLogCtrl($uibModalInstance, ApiService, $routeParams, params) {
        /* jshint validthis: true */
        var vm = this;
        vm.activate = activate;

        vm.modalInstance = $uibModalInstance;
        vm.close = $uibModalInstance.dismiss;
        vm.logType = params.logType;
        vm.getLogData = getLogData;
        activate();

        ////////////////

        function activate() {
            getLogData();
        }

        function getLogData()
        {
            ApiService.getLog(params.productName, params.agent, params.logType).then(getSuccessData).catch(getErrorData);
        }
        function getErrorData(error) {
//            logger.info('Some Error Occured');
        }

        function getSuccessData(response) {
            vm.logData = response.data;
        }

    }
})();