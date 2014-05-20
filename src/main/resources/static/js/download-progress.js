        var ws = null;

        function connect(url) {
            ws = new SockJS(url);
            ws.onopen = function () {
                log('Info: WebSocket connection opened.');
                sendFileName();
            };
            ws.onmessage = function (event) {
                updateBar(event.data);
            };
            ws.onclose = function () {
                log('Info: WebSocket connection closed.');
            };
        }
        
        function sendFileName(){
        
  			var filenameElem = document.getElementById('filename');
  			var filename = filenameElem.innerHTML;
  			if(ws != null){
  				ws.send(filename);
  			}
  			
  			log('filename send complete.');
        }

        function disconnect() {
            if (ws != null) {
                ws.close();
                ws = null;
            }
        }

        function updateBar(percent) {
            var bar = document.getElementById('progress-bar');
  			bar.style.width = (percent) +'%';
  			if(percent == 100){
  				disconnect();
  			}
        }
        
        function log(msg){
        	//console.log(msg);
        }
       
      	//window.onLoad = start;
