
(function() {
    'use strict';

    angular.module('app.core')
        .config(configure)
        .constant('_', window._);

    function configure($httpProvider) {
        $httpProvider.interceptors.push('httpInterceptor');
    }
})();