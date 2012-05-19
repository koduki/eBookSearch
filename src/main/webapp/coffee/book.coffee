validate = (isbn) =>
    msg = if isbn == ""
        "ISBNを入力してください"
    else if isbn.replace(/-/g,"").length != 13
        "ISBNは13桁で入力してください"
    else
        ""

    if msg != ""
        alert(msg)
        false
    else
        true

$ ()->
    $(".isbn").map (i, x) -> 
        input = $(x).find("*[name=isbn]")
        submit = $(x).find("*[type=submit]")
        submit.click () -> validate(input.val())
