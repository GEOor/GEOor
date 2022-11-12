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
    // 마커로 설정할 필요가 없는 경우 return
    const $option = document.getElementById(hazardName)
    if (!$option.checked) return;

    // 마커를 설정할 vectorLayer를 추가
    map.addLayer(vectorLayer);

    try {
        // api 호출 후 데이터를 파싱
        const res = await fetch('http://localhost:8080/hazard/' + hazardName);
        const data = await res.json();

        if (data.error) throw data.error;

        // api 호출을 통해 얻어낸 데이터를 이용해 마커를 생성
        data.forEach((coord, i) => vectorLayer.getSource().addFeature(createMarker(coord, i)));
    } catch (error) {
        console.error(error);
    }
}

const requestHillShade = async () => {
    const $address = document.getElementById('address');
    const $date = document.getElementById('date');
    const $time = document.getElementById('time');

    const res = await fetch('/hillShade', { 
        method: 'POST',
        headers: {
            "Content-Type": "application/json; charset=UTF-8"
        },
        body: JSON.stringify({ 
            address: $address.value, 
            date: $date.value,
            time: $time.value
        })
    });
    const { latitude, longitude, cityId } = await res.json()

    // 검색한 지역 쪽으로 지도를 이동
    map.getView().setCenter([parseFloat(latitude), parseFloat(longitude)]);
    map.getView().setZoom(16);

    // 도로 데이터를 geoserver로부터 받아와 map에 표시
    map.addLayer(new ol.layer.Tile({
        visible: true,
        source: new ol.source.TileWMS({
            url: 'http://localhost:8600/geoserver/geor/wms', //행정구역 16개 따로?
            params: {
                FORMAT: 'image/png',
                TILED : true,
                LAYERS: 'geor:road',
                CQL_FILTER: 'sig_cd = ' + cityId
            }
        })
    }));

    return [Number(latitude), Number(longitude)];
}

// 검색할 날짜의 범위를 제한
const inputDataRange = () => {
    const $date = document.getElementById('date');

    // 오늘 날짜
    const today = new Date();
    // 일주일 뒤
    const weekLater = new Date(today.getTime() + (7*24*60*60*1000));
    
    const dateToString = (date) => date.toISOString().split("T")[0];

    $date.valueAsDate = today;
    $date.min = dateToString(today);
    $date.max = dateToString(weekLater);
}

// 사용자가 입력한 값을 이용해 hillShade 알고리즘 실행
const analysisStart = (e) => {
    e.preventDefault();

    // 이전에 생성한 마커 레이어 제거
    map.removeLayer(vectorLayer);

    //1. 사용자가 입력한 위치 -> 위,경도 변환 후 지도 내 카메라 줌
    requestHillShade();

    //2. 사용자가 입력한 (위치, 날짜, 시간) -> 알맞은 wms를 받아올 수 있는 api 호출
    //map.addLayer(wmsLayer);

    //3. (교량, 터널, 상습결빙구역) -> 마커 생성
    // setHazardMarker("tunnel");
    // setHazardMarker("bridge");
    setHazardMarker("frozen");
}

const $form = document.getElementById("form");
$form.onsubmit = analysisStart;

// 날짜 범위 설정
inputDataRange();
