
(function() {
    'use strict';

    angular.module('app.core')
        .directive("loader", function ($rootScope) {
            return function ($scope, element, attrs) {
                $scope.$on("loader_show", function () {
                    return element.removeClass('ng-hide');
                });
                return $scope.$on("loader_hide", function () {
                    return element.addClass('ng-hide');
                });
            };
        }
    );
})();