(function () {
    'use strict';

    angular
        .module('dragonConsoleApp')
        .factory('Register', Register);

    Register.$inject = ['$resource'];

    function Register ($resource) {
        return $resource('api/register', {}, {});
    }
})();
