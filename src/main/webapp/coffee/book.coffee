validate = (input, submit) =>
    submit.click () ->
        if (input.val() == "")
            alert("ISBNを入力してください")
            false
        else
            true
$ ()->
    $(".isbn").map (i, x) -> 
        validate($(x).find("*[name=isbn]"), $(x).find("*[type=submit]"))
