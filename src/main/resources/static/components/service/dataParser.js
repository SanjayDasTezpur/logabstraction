
(function () {
    'use strict';
    angular
        .module('app.services')
        .factory('loggerDataParser', loggerDataParser);

    function loggerDataParser() {
        var service = {
            removeFieldsFromData: removeFieldsFromData,
            renamedFieldsInData: renamedFieldsInData
        };

        return service;
    }

    function removeFieldsFromData(data, removeFields) {
        for (var j = 0; j < data.length; j++) {
            for (var i = 0; i < removeFields.length; i++) {
                delete data[j][removeFields[i]];
            }
        }
        return data;
    }

    function renamedFieldsInData(data, renamedFields) {
        for (var j = 0; j < data.length; j++) {
            for (var fields in renamedFields) {
                data[j][renamedFields[fields]] = data[j][fields];
                delete data[j][fields];
            }
        }
        return data;
    }
}());
