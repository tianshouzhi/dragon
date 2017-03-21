(function() {
    'use strict';

    angular
        .module('dragonConsoleApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('register', {
            parent: 'account',
            url: '/register',
            data: {
                authorities: [],
                pageTitle: 'Registration'
            },
            views: {
                'content@': {
                    templateUrl: 'app/account/register/register.html',
                    controller: 'RegisterController',
                    controllerAs: 'vm'
                }
            }
        });
    }
})();
