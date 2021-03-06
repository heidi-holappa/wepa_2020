
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Bootstrap 4 Website Example</title>
    <meta charset="utf-8" xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    <style>
        .fakeimg {
            height: 200px;
            background: #aaa;
        }
    </style>
</head>
<body>
    
    <div th:replace="fragments/layout :: pageTitle">Page title</div>
    <nav th:replace="fragments/layout :: nav">Navigation</nav>
    

    <div class="container" style="margin-top:30px">
        <div class="row">
            <div class="col-sm">
                <h1>Please submit your username and password</h2>
                <form name='f' action="login" method='POST'>
                    <table>
                        <tr>
                            <td>User:</td>
                            <td><input type='text' name='username' value=''></td>
                        </tr>
                        <tr>
                            <td>Password:</td>
                            <td><input type='password' name='password' /></td>
                        </tr>
                        <tr>
                            <td><input name="submit" type="submit" value="submit" /></td>
                        </tr>
                    </table>
                   
                </form>
                <p>
                    Don't have an account? Create one <a th:href="@{/auth/signup}">here.</a>.
                </p>
                
            </div>
        </div>
    </div>

    <div class="jumbotron text-center" style="margin-bottom:0">
        <footer th:replace="fragments/layout :: footer">Footer comes here</footer>
    </div>

</body>
</html>

