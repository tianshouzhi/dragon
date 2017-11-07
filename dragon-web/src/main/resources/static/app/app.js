/**
 * Created by TIANSHOUZHI336 on 2017/3/26.
 */
var DragonConsoleApp=angular.module("DragonConsole",["ngResource","ui.router"]);
/* Setup Rounting For All Pages */
DragonConsoleApp.config(["$stateProvider","$urlRouterProvider",function ($stateProvider,$urlRouterProvider) {
    // Redirect any unmatched url
    $urlRouterProvider.otherwise("/");
}]);