var app = angular.module("TraderApp", []);

app.controller("ExchangeController", function ($scope, $http) {

    $scope.isAnyAssignment = function() {
        var result = false;
        for(var i=0; i< $scope.exchanges.length; i++) {
            if($scope.exchanges[i].active) {
                return true;
            }
        }
        return result;
    }

    $scope.assign = function (exchangeId) {
        $http.post('/rest/sessions/assign/' + exchangeId).success(function (data, status, headers, config) {
            for (var i = 0; i < $scope.assignments.length; i++) {
                var s = $scope.assignments[i];
                if (s.id == exchangeId) {
                    $scope.assignments[i].active = true;
                    break;
                }
            }
        }).error(function (data, status, headers, config) {
            if (data.message == 'Time is out') {
                console.log("Timeout")
            }
        });
    }

    $scope.getExchanges = function () {
        $http.get('/public/rest/exchange').success(function (data, status, headers, config) {
            $scope.exchanges = data;
        }).error(function (data, status, headers, config) {
            if (data.message == 'Time is out') {
                console.log("Timeout")
            }
        });
    }

    $scope.getAssignments = function () {
        $http.get('/rest/session/assignments').success(function (data, status, headers, config) {
            $scope.assignments = data;
        }).error(function (data, status, headers, config) {
            if (data.message == 'Time is out') {
                console.log("Timeout")
            }
        });
    }
});

app.controller("UserAssignmentController", function ($scope, $http) {
    $scope.getCategories = function () {
        $http.get('/public/rest/exchange').success(function (data, status, headers, config) {
            $scope.exchanges = data;
        }).error(function (data, status, headers, config) {
            if (data.message == 'Time is out') {
                console.log("Timeout")
            }
        });
    }
});

app.controller("SessionController", function ($scope, $http, $filter) {
    $scope.getSessions = function () {
        $http.get('/rest/sessions').success(function (data, status, headers, config) {
            var sessions = data;
            for (var i = 0; i < sessions.length; i++) {
                var session = sessions[i];
                session.viewLink = "/view/" + session.id;
                session.publishDate = $filter('date')(session.publishDate);
            }
            $scope.sessionList = sessions;
        }).error(function (data, status, headers, config) {
            if (data.message == 'Time is out') {
                $scope.finishByTimeout();
            }
        });
    }
});

app.controller("BrowseController", function ($scope, $http, $filter, $window) {
    $scope.getSessions = function () {
        $http.get('/rest/sessions/active').success(function (data, status, headers, config) {
            var sessions = data;
            for (var i = 0; i < sessions.length; i++) {
                var session = sessions[i];
                session.publishDate = $filter('date')(session.publishDate);
                session.viewLink = "/view/" + session.id;
            }
            $scope.sessionList = session;
        }).error(function (data, status, headers, config) {
            console.error(status, data, headers);
        });
    }

    $scope.delete = function (id) {
        $http.delete('/rest/sessions/stop/' + id).success(function (data, status, headers, config) {
            for (var i = 0; i < $scope.sessionList.length; i++) {
                var j = $scope.sessionList[i];
                if (j.id == id) {
                    $scope.sessionList.splice(i, 1);
                    break;
                }
            }
        }).error(function (data, status, headers, config) {
            console.error(status, data, headers);
        });
    }

    $scope.view = function (id) {
        for (var i = 0; i < $scope.sessionList.length; i++) {
            var j = $scope.sessionList[i];
            if (j.id == id) {
                $window.location.href = $scope.sessionList[i].viewLink;
            }
        }
    }
});
