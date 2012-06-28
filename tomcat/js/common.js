function createHttpRequest() {

    if(window.ActiveXObject){
        try {
            // MSXML2
            return new ActiveXObject("Msxml2.XMLHTTP");
        } catch (e) {
            try {
                // MSXML
                return new ActiveXObject("Microsoft.XMLHTTP");
            } catch (e2) {
                return null;
            }
        }

    } else if(window.XMLHttpRequest){
        return new XMLHttpRequest();
    } else {
        return null;
    }
}

function postMessage( user , message , fileName , async ) {
    var httpoj = createHttpRequest();

    httpoj.open( 'POST' , fileName , async );
    httpoj.setRequestHeader('Content-Type', 
        'application/x-www-form-urlencoded; charset=UTF-8');

    httpoj.onreadystatechange = function() {
        if (httpoj.readyState==4) {
            on_loaded(httpoj);
        }
    }

    httpoj.send( 'user=' + user + '&message=' + message );
}
function on_loaded(oj) {
    res = oj.responseText;
}
