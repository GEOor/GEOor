// 대한민국 지도가 한눈에 들어오는 위치를 기준으로
// VWorld 지도를 openlayers를 이용해 표시
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
        center: ol.proj.transform([127.6176, 36.8724], 'EPSG:4326', 'EPSG:3857'),
        zoom: 7,
        minZoom: 7,
        maxZoom: 19
    })
});

// 각 마커를 표시할 Vector 레이어
const vectorLayer = new ol.layer.Vector({
    source: new ol.source.Vector(),
    style: new ol.style.Style({
        image: new ol.style.Icon({
            scale: 0.05,
            src: "img/marker.png"
        })
    })
});


// 주어진 좌표에 주어진 id를 갖는 마커 생성
const createMarker = (coord, id) => {
    const {latitude, longitude} = coord
    const geometry = new ol.geom.Point(ol.proj.fromLonLat([parseFloat(longitude), parseFloat(latitude)]))
    return new ol.Feature({ geometry, id });
}

// tunnel, bridge, frozen 중 하나를 인자로 제공한 경우
// 각각 터널, 교량, 결빙 상태를 지도에 마커로 표시
const setHazardMarker = async (hazardName) => {
    const $option = document.getElementById(hazardName)
    if (!$option.value) return;

    map.addLayer(vectorLayer);

    try {
        // api 호출 후 데이터를 파싱
        const res = await fetch('http://localhost:8080/hazard/' + hazardName);
        const data = await res.json();

        if (data.error) return;

        // api 호출을 통해 얻어낸 데이터를 이용해 마커를 생성
        data.forEach((coord, i) => vectorLayer.getSource().addFeature(createMarker(coord, i)))
    } catch (error) {
        console.error(error)
    }
}

const requestHillShade = (latitude, longitude, districtNumber) => {
    const $date = document.getElementById('date');
    const $time = document.getElementById('time');

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
}

/** @todo request 보내는 부분과 지도 이동하는 부분 나누기 */
// 현재로서는 역할이 명확하지 않음
// geoserver에서 도로 데이터를 받아와 지도에 그림
const addressToCoordinates = () => {
    const $address = document.getElementById('address');

    const request = new XMLHttpRequest();

    request.open("GET","http://api.vworld.kr/req/address?service=address&request=getcoord&version=2.0"
        + "&crs=epsg:3857"
        + "&address=" + encodeURI($address.value) + "&refine=true&simple=false&format=xml&type=road"
        + "&key=49EA5D21-2E61-3344-82B1-9E3F0B6C5805");
    request.send();
    request.onreadystatechange = /* async */ () => {
        if (request.readyState !== 4) return
        if (request.status < 200 || request.status >= 300)
            return alert(request.status);

        // 로딩창 on
        loadingOn()

        const { responseXML } = request
        //console.log(responseXML);

        const getXMLValue = (name) => responseXML.getElementsByTagName(name)[0].childNodes[0].nodeValue

        // 검색한 지역의 좌표
        const latitude = getXMLValue('x');
        const longitude = getXMLValue('y');
        // 검색한 지역의 행정구역 번호
        const districtNumber = getXMLValue('level4AC')?.substr(0, 5);

        // 검색한 지역 쪽으로 지도를 이동
        map.getView().setCenter([parseFloat(latitude), parseFloat(longitude)]);
        map.getView().setZoom(16);

        requestHillShade(latitude, longitude, districtNumber)

        // 도로 데이터를 geoserver로부터 받아와 map에 표시
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

        return [Number(latitude), Number(longitude)];
    }
}

// 검색할 날짜의 범위를 제한
const inputDataRange = () => {
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

// 사용자가 입력한 값을 이용해 hillShade 알고리즘 실행
const analysisStart = () => {

    // 이전에 생성한 마커 레이어 제거
    map.removeLayer(vectorLayer);

    //1. 사용자가 입력한 위치 -> 위,경도 변환 후 지도 내 카메라 줌
    addressToCoordinates();

    //2. 사용자가 입력한 (위치, 날짜, 시간) -> 알맞은 wms를 받아올 수 있는 api 호출
    //map.addLayer(wmsLayer);

    //3. (교량, 터널, 상습결빙구역) -> 마커 생성
    // setHazardMarker("tunnel");
    // setHazardMarker("bridge");
    // setHazardMarker("frozen");

}

// 날짜 범위 설정
inputDataRange();

/* 로딩창 */
const modal = document.getElementById("modal")
const loadingOn = () =>  { }
const loadingOff = () =>  { }