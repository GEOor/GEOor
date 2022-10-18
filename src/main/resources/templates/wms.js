// vworld 배경지도 띄우기
const map = new ol.Map({
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
// let wmsLayer = new ol.layer.Tile({
//     visible: true,
//     source: new ol.source.TileWMS({
//         url: 'http://localhost:8600/geoserver/geor/wms',
//         params: {
//             'FORMAT': 'image/png',
//             'TILED' : true,
//             'LAYERS': 'geor:road'
//         }
//     })
// })

/*
    가장 큰 단위인 레이어 선언부
    feature 단위의 스타일 속성을 지정할 수 있다.
 */
const vectorLayer = new ol.layer.Vector({
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
const createMarker = (coord, id) => {
    const {latitude, longitude} = coord
    const geometry = new ol.geom.Point(ol.proj.fromLonLat([parseFloat(longitude), parseFloat(latitude)]))
    return new ol.Feature({ geometry, id });
}

/*
    input: hazardName(tunnel, bridge, frozen)
    vectorlayer - source에다가 feature(좌표, 아이콘으로 구성) 넣기
    즉, layer - source - feature 순으로 단위가 형성되어 있다. (내림차순)
 */
const setHazardMarker = async (hazardName) => {
    const $option = document.getElementById(hazardName)
    if (!$option.value) return;

    map.addLayer(vectorLayer);

    try {
        // api 호출 후 데이터를 파싱
        const res = await fetch('http://localhost:8080/hazard/' + hazardName);
        const data = await res.json();

        // api 호출을 통해 얻어낸 데이터를 이용해 마커를 생성
        data.forEach((coord, i) => vectorLayer.getSource().addFeature(createMarker(coord, i)))
    } catch (error) {
        console.error(error)
    }

    // fetch('http://localhost:8080/hazard/' + hazardName)
    //     .then(res => res.json())
    //     .then(data => {

    //         let lat = [];
    //         let lon = [];

    //         for(let i=0; i<data.length; i++) {
    //             lat.push(data[i].latitude);
    //             lon.push(data[i].longitude);
    //         }

    //         for(let i=0; i<data.length; i++) {
    //             vectorLayer.getSource().addFeature(createMarker(lat[i], lon[i], i));
    //         }
    //     })
    //     .catch(err => {
    //         alert(arr);
    //         //console.log(err);
    //     })
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

    //console.log("x: " + x + " y: " + y);
    return [x, y];
}

//주소를 xy좌표로 변환하기
function addressToCoordinates() {
    const request = new XMLHttpRequest();

    const $address = document.getElementById('address');
    const $date = document.getElementById('date');
    const $time = document.getElementById('time');

    request.open("GET","http://api.vworld.kr/req/address?service=address&request=getcoord&version=2.0"
        + "&crs=epsg:3857"
        + "&address=" + encodeURI($address.value) + "&refine=true&simple=false&format=xml&type=road"
        + "&key=49EA5D21-2E61-3344-82B1-9E3F0B6C5805");
    request.send();
    request.onreadystatechange = /* async */ function() {
        if (request.readyState !== 4) return
        if (request.status < 200 || request.status >= 300)
            return alert(request.status);

        // 로딩창 on
        loadingOn()

        const { responseXML } = request
        //console.log(responseXML);

        const getXMLValue = (name) => responseXML.getElementsByTagName(name)[0].childNodes[0].nodeValue

        // 검색한 지역의 좌표
        const xCoordinate = getXMLValue('x');
        const yCoordinate = getXMLValue('y');
        // 검색한 지역의 행정구역 번호
        const districtNumber = getXMLValue('level4AC')?.substr(0, 5)

        const [latitude, longitude] = ol.proj.transform([xCoordinate, yCoordinate], 'EPSG:3857', 'EPSG:4326');
        lookAtMe(latitude, longitude);

        /** @todo /hillShade가 제대로 작동한다면 주석 풀고 버그 수정할 것 */
        // await fetch("http://localhost:8080/hillShade/", {
        //     method: "POST",
        //     headers: {
        //         "Content-Type": "application/json; charset=UTF-8"
        //     },
        //     body: JSON.stringify({
        //         latitude,
        //         longitude,
        //         cityId: districtNumber,
        //         date: $date.value,
        //         time: $time.value.split(":")[0],
        //     }),
        // });

        //console.log(response)

        // wms layer 생성
        map.addLayer(new ol.layer.Tile({
            visible: true,
            source: new ol.source.TileWMS({
                url: 'http://localhost:8600/geoserver/geor/wms', //행정구역 16개 따로?
                params: {
                    FORMAT: 'image/png',
                    TILED : true,
                    LAYERS: 'geor:road',
                    CQL_FILTER: 'sig_cd = ' + districtNumber
                }
            })
        }));

        // 로딩창 off
        loadingOff()

        return [Number(xCoordinate), Number(yCoordinate)];
    }
}

// 입력받는 날짜범위 수정하기
function inputDataRange(){
    const $date = document.getElementById('date')

    // 오늘 날짜
    const today = new Date();
    // 일주일 뒤
    const weekLater = new Date(today.getTime() + (7*24*60*60*1000));
    
    const dateToString = (date) => date.toISOString().split("T")[0]

    $date.valueAsDate = today;
    $date.min = dateToString(today)
    $date.max = dateToString(weekLater);
}

/*
    분석 시작 버튼(#id:analysisButton) 클릭 이벤트 발생 시 실행되는 함수
 */
function analysisStart(){

    //이전에 생성한 마커 레이어 제거
    map.removeLayer(vectorLayer);

    //1. 사용자가 입력한 위치 -> 위,경도 변환 후 지도 내 카메라 줌
    addressToCoordinates();

    //2. 사용자가 입력한 (위치, 날짜, 시간) -> 알맞은 wms를 받아올 수 있는 api 호출
    //map.addLayer(wmsLayer);

    //3. (교량, 터널, 상습결빙구역) -> 마커 생성
    setHazardMarker("tunnel");
    setHazardMarker("bridge");
    setHazardMarker("frozen");

}

// 날짜 범위 설정
inputDataRange();

/* 로딩창 */
const modal = document.getElementById("modal")
function loadingOn() {  }
function loadingOff() { }