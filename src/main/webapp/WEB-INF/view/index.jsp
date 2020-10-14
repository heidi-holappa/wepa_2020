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
            <div class="col-sm-4">
                <h2>Show when logged in</h2>
                <h5>Welcome, "nimi"</h5>
                <div class="fakeimg">Profile picture</div>
                <p>Basic information about user here </p>
                <h3>Some Links</h3>
                <p>Quick navigation:</p>
                <ul class="nav nav-pills flex-column">
                    <li class="nav-item">
                        <a class="nav-link active" href="#">Home</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="/profile_view">Profile</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="/auth/login">Sign in</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link disabled" href="#">If logged in active, if not, disabled?</a>
                    </li>
                </ul>
                <hr class="d-sm-none">
            </div>
            <div class="col-sm-8">
                <h2>Post feed</h2>
                <h5>Title description, maybe date?</h5>
                <p>Feeds start here. Each feed includes, profile pic, post content, comments. If logged in, you can like and comment</p>
                <p>Sunt in culpa qui officia deserunt mollit anim id est laborum consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco.</p>
                <br>

            </div>
        </div>
    </div>
    
    


    <div th:replace="fragments/layout :: footer">Footer comes here</div>

</body>
</html>

