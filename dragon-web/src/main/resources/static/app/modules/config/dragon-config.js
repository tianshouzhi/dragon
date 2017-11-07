/**
 * Created by TIANSHOUZHI336 on 2017/3/26.
 */
var DragonConsoleApp = angular.module("DragonConsole");
DragonConsoleApp.config(function ($stateProvider) {
    $stateProvider.state("create-config",{
        templateUrl:"app/modules/config/dragon-config.html",
        controller:"CreateConfigController"
    });
});
DragonConsoleApp.controller("CreateConfigController", function ($scope, $resource) {
    $scope.shardTypes = [
        {enName: "ShardDBAndTB", cnName: "分库分表"},
        {enName: "OnlyShardTB", cnName: "只分表"},
        {enName: "OnlyShardDB", cnName: "只分库"}];

    $scope.namingStyles=[
        {enName: "scalable", cnName: "可扩展命名"},
        {enName: "sequence", cnName: "序列增长"},
        {enName: "same", cnName: "表名相同"}];

    $scope.datasources=["ha","druid","c3p0","dbcp"];

    $scope.currentShardType=$scope.shardTypes[0];
    $scope.currentNamingStyle=$scope.namingStyles[0];
    $scope.currentDatasource=$scope.datasources[0];

    $scope.setShardType=function (shardType) {
        $scope.currentShardType=shardType;
    }
    $scope.setNamingStyle=function(namingStyle){
        $scope.currentNamingStyle=namingStyle;
    }
    $scope.setDatasource=function(datasource){
        $scope.currentDatasource=datasource;
    }
    
    $scope.caculateDBNamePattern=function () {
        if($scope.logicDBName==null|| $scope.currentShardType=="OnlyShardTB"||$scope.maxDBNum==null){
            return;
        }
        var zeroFillNum="0";
        if($scope.currentNamingStyle==namingStyles[0]){

        }
        return logicDBName+"_{0,number,#"+zeroFillNum+"}";

    }
});