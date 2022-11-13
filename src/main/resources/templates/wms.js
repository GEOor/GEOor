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
        center: ol.proj.transform([127.9, 36], 'EPSG:4326', 'EPSG:3857'),
        zoom: 7.3,
        minZoom: 7,
        maxZoom: 19
    })
});

// 마커의 종류
const markerTypes = ['tunnel', 'bridge', 'frozen'];

// 마커를 담을 레이어의 집합
const markerLayers = markerTypes.reduce((prev, type) => ({
    ...prev, 
    [type]: new ol.layer.Vector({
        source: new ol.source.Vector(),
        style: new ol.style.Style({
            image: new ol.style.Icon({
                scale: 0.05,
                src: `img/${type}.png`
            })
        })
    })
}), {})

// 각 마커를 담을 레이어를 map에 추가
markerTypes.forEach(name => map.addLayer(markerLayers[name]))

// 주어진 좌표에 주어진 id를 갖는 마커 생성
const createMarker = (coord, id) => {
    const {latitude, longitude} = coord
    const geometry = new ol.geom.Point(ol.proj.fromLonLat([parseFloat(longitude), parseFloat(latitude)]))
    return new ol.Feature({ geometry, id });
}

// tunnel, bridge, frozen 중 하나를 인자로 제공한 경우
// 각각 터널, 교량, 결빙 상태를 지도에 마커로 표시
const setMarkers = async (markerType) => {
    // 마커로 설정할 필요가 없는 경우 return
    const $option = document.getElementById(markerType)
    if (!$option.checked) return;

    try {
        // 마커를 담을 레이어를 생성
        const markerLayer = markerLayers[markerType]

        // api 호출 후 데이터를 파싱
        const res = await fetch(`http://localhost:8080/hazard/${markerType}`);
        const data = await res.json();
        if (data.error) throw data.error;

        // api 호출을 통해 얻어낸 데이터를 이용해 마커를 생성
        data.forEach((coord, i) => markerLayer.getSource().addFeature(createMarker(coord, i)));
    } catch (error) {
        console.error(error);
    }
}

const { checkLayer, getLayer, createLayer } = (() => {
    const layers = {}

    const checkLayer = (cityId) => cityId in layers
    const getLayer = (cityId) => layers[cityId]
    const createLayer = (cityId) => layers[cityId] = new ol.layer.Tile({
        visible: true,
        source: new ol.source.TileWMS({
            url: 'http://localhost:8600/geoserver/geor/wms', //행정구역 16개 따로?
            params: {
                FORMAT: 'image/png',
                TILED : true,
                LAYERS: 'geor:road',
                CQL_FILTER: `sig_cd = ${cityId}`
            }
        })
    })

    return { checkLayer, getLayer, createLayer }
})()

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
    if (checkLayer(cityId)) map.removeLayer(getLayer(cityId))
    map.addLayer(createLayer(cityId));
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

const setLoading = (toggle) => {
    const $loading = document.getElementById('loading') 
    const MODAL_HIDDEN = 'modal-hidden'

    $loading.classList.toggle(MODAL_HIDDEN, !toggle)
}

// 사용자가 입력한 값을 이용해 hillShade 알고리즘 실행
const analysisStart = async (e) => {
    e.preventDefault();

    // 로딩창을 노출
    setLoading(true);

    // 이전에 생성한 마커 레이어 제거
    markerTypes.forEach(name => markerLayers[name].getSource().clear())
    // 사용자가 입력한 위치 -> 위,경도 변환 후 지도 내 카메라 줌
    await requestHillShade();
    // (교량, 터널, 상습결빙구역) -> 마커 생성
    await Promise.all(markerTypes.map(setMarkers))

    // 로딩창을 제거
    setLoading(false);
}

const $form = document.getElementById("form");
$form.onsubmit = analysisStart;

// 날짜 범위 설정
inputDataRange();
