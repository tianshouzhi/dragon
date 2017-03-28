var DragonConsole = angular.module("DragonConsole");
DragonConsole.factory("UserService", ["$resource", function ($resource) {
    return $resource(
        "/users/:userId",
         {userId:"@userId"},//不提供默认参数
        {"update": {"method": "PUT"}}//扩展一个update方法，用于更新操作
    )
}]);

DragonConsole.controller("UserController", ["$scope", "UserService", function ($scope, UserService) {
    //利用双向绑定机制修改其中的参数
    $scope.queryParams = {
        page: 0,
        size: 20,
        keyword: "tianshouzhi"
    }

    $scope.user={userId:10,name:"tianshozhi"}
    
    $scope.search = function () {
        UserService.query($scope.queryParams, function (data, headers) {//查询所有，不带任何参数
            $scope.users = data;
        });
    }

    $scope.findById = function () {
        UserService.get({userId:$scope.user.userId}, function (data) {
            $scope.userDetails = data;
        })
    }
    $scope.delete = function () {
        UserService.delete($scope.user, function (data, headers) {
        })
    }
    $scope.save = function () {
        UserService.save(
            $scope.user,//requestBody
            onSaveSuccess,
            onSaveError);
    }
    $scope.update = function () {
        UserService.update($scope.user, onSaveSuccess, onSaveError)
    }

    var onSaveSuccess = function (result) {
    }
    var onSaveError = function (result) {
    }
}]);
