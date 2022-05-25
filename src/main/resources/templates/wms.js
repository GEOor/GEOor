// vworld 배경지도 띄우기
let map = new ol.Map({
    target: 'map',
    layers: [
        new ol.layer.Tile({
            source: new ol.source.XYZ({
                url: 'http://xdworld.vworld.kr:8080/2d/Base/202002/{z}/{x}/{y}.png'
            })
        }),
    ],
    view: new ol.View({
        center: convertCoordinates(127.6176, 36.8724),
        zoom: 7,
        minZoom: 7,
        maxZoom: 19
    })
});

// wms layer 생성
let wmsLayer = new ol.layer.Tile({
    visible: true,
    source: new ol.source.TileWMS({
        url: 'http://localhost:8088/geoserver/GeoOr/wms',
        params: {
            'FORMAT': 'image/png',
            'TILED' : true,
            'LAYERS': 'GeoOr:road'
        }
    })
})

/*
    가장 큰 단위인 레이어 선언부
    feature 단위의 스타일 속성을 지정할 수 있다.
 */
let vectorLayer = new ol.layer.Vector({
    source: new ol.source.Vector(),
    style: new ol.style.Style({
        image: new ol.style.Icon({
            scale: 0.05,
            src: "img/marker.png"
        })
    })
});

/*
    input : 위도, 경도, id(must unique)
    source에 넣을 feature를 생성하는 함수 (마커 생성기)
 */
function createMarker(lng, lat, id) {
    return new ol.Feature({
        geometry: new ol.geom.Point(ol.proj.fromLonLat([parseFloat(lng), parseFloat(lat)])),
        id: id
    });
}

/*
    input: hazardName(tunnel, bridge, frozen)
    vectorlayer - source에다가 feature(좌표, 아이콘으로 구성) 넣기
    즉, layer - source - feature 순으로 단위가 형성되어 있다. (내림차순)
 */
function showHazardMarker(hazardName) {

    map.addLayer(vectorLayer);

    //api 호출 부분
    fetch('http://localhost:8080/hazard/' + hazardName)
        .then(res => res.json())
        .then(data => {

            let lat = [];
            let lon = [];

            for(let i=0; i<data.length; i++) {
                lat.push(data[i].latitude);
                lon.push(data[i].longitude);
            }

            for(let i=0; i<data.length; i++) {
                vectorLayer.getSource().addFeature(createMarker(lat[i], lon[i], i));
            }
        })
        .catch(err => {
            console.log(err);
        })
}


function lookAtMe(lat, lng) {

    /*
        우리가 알고 있는 위,경도 좌표계(EPSG:4326) -> 구글 맵 좌표계(EPSG:3857) 변환
        EPSG:4326..ex) (37.5, 131.2)
     */
    map.getView().setCenter(ol.proj.transform([lat, lng], 'EPSG:4326', 'EPSG:3857'));
    map.getView().setZoom(16);
}

//EPSG:4326 -> EPSG:3857
function convertCoordinates(lon, lat) {
    let x = (lon * 20037508.34) / 180;
    let y = Math.log(Math.tan(((90 + lat) * Math.PI) / 360)) / (Math.PI / 180);
    y = (y * 20037508.34) / 180;

    console.log("x: " + x + " y: " + y);
    return [x, y];
}

//주소를 xy좌표로 변환하기
function addressToCoordinates(address) {
    let request = new XMLHttpRequest();
    let xCoordinate, yCoordinate;

    request.open("GET","http://api.vworld.kr/req/address?service=address&request=getcoord&version=2.0"
        + "&crs=epsg:3857" // EPSG:3857
        + "&address=" + encodeURI(address) + "&refine=true&simple=false&format=xml&type=road"
        + "&key=4480D1CC-812E-33AC-8A4D-05E08AC71B7A");
    request.send();
    request.onreadystatechange = function() {
        if (request.readyState === 4) {
            if (request.status >= 200 && request.status < 300) {
                let xml = request.responseXML;
                xCoordinate = xml.getElementsByTagName('x')[0].childNodes[0].nodeValue;
                yCoordinate = xml.getElementsByTagName('y')[0].childNodes[0].nodeValue;

                let tmpx, tmpy;

                [tmpx, tmpy] = ol.proj.transform([xCoordinate, yCoordinate], 'EPSG:3857', 'EPSG:4326');
                lookAtMe(tmpx, tmpy);

                return [Number(xCoordinate), Number(yCoordinate)];
            } else {
                alert(request.status);
            }
        }
    }

}

//입력받는 날짜범위 수정하기
function inputDataRange(){
    let today = new Date();

    //오늘 날짜
    let year = today.getFullYear();
    let month = today.getMonth() + 1;
    month = month < 10 ? '0' + month : month;
    let date = today.getDate();
    date = date < 10 ? '0' + date : date;

    document.getElementById('date').value = String(year + '-' + month + '-' + date);
    document.getElementById('date').min = String(year + '-' + month + '-' + date);

    //일주일 뒤
    let weekDay = today.getTime() + (7*24*60*60*1000);
    today.setTime(weekDay);

    let weekYear = today.getFullYear();
    let weekMonth = today.getMonth() + 1;
    weekMonth = weekMonth < 10 ? '0' + weekMonth : weekMonth;
    let weekDate = today.getDate();
    weekDate = weekDate < 10 ? '0' + weekDate : weekDate;

    document.getElementById('date').max = String(weekYear + '-' + weekMonth + '-' + weekDate);
}

function inputAddress(){
    let addressInput = document.getElementById('address').value;
    console.log("입력한 주소: ", addressInput);
    return addressInput;
}

function inputDate(){
    let dateInput = document.getElementById('date').value;
    console.log("입력한 날짜: ", dateInput);
    return dateInput;
}

function inputTime(){
    let timeInput = document.getElementById('time').value;
    timeInput = String(timeInput).substring(0, 2);
    console.log("입력한 시간: ", timeInput);
    return timeInput;
}

function inputTunnel(){
    let tunnelOption = document.getElementById('tunnelCheck').checked;
    console.log("터널 옵션: ", tunnelOption);

    if(tunnelOption) showHazardMarker("tunnel");

    return tunnelOption;
}

function inputBridge(){
    let bridgeOption = document.getElementById('bridgeCheck').checked;
    console.log("교량 옵션: ", bridgeOption);

    if(bridgeOption) showHazardMarker("bridge");

    return bridgeOption;
}

function inputFrost(){
    let frostOption = document.getElementById('frostCheck').checked;
    console.log("결빙 옵션: ", frostOption);

    if(frostOption) showHazardMarker("frozen");
    return frostOption;
}

/*
    분석 시작 버튼(#id:analysisButton) 클릭 이벤트 발생 시 실행되는 함수
 */
function analysisStart(){

    //이전에 생성한 마커 레이어 제거
    map.removeLayer(vectorLayer);

    //1. 사용자가 입력한 위치 -> 위,경도 변환 후 지도 내 카메라 줌
    addressToCoordinates(inputAddress());

    //2. 사용자가 입력한 (위치, 날짜, 시간) -> 알맞은 wms를 받아올 수 있는 api 호출
    inputDate();
    inputTime();
    //map.addLayer(wmsLayer);

    //3. (교량, 터널, 상습결빙구역) -> 마커 생성
    inputTunnel();
    inputBridge();
    inputFrost();

}

<!-- 날짜 범위 설정 -->
inputDataRange();