const arrToObj = (arr, func) =>
    arr.reduce((acc, val) => ((acc[val] = func(val)), acc), {});

// 대한민국 지도가 한눈에 들어오는 위치를 기준으로
// VWorld 지도를 openlayers를 이용해 표시
const map = new ol.Map({
    target: "map",
    layers: [
        new ol.layer.Tile({
            source: new ol.source.XYZ({
                url: "http://xdworld.vworld.kr:8080/2d/Base/202002/{z}/{x}/{y}.png",
            }),
        }),
    ],
    view: new ol.View({
        center: ol.proj.transform([127.9, 36], "EPSG:4326", "EPSG:3857"),
        zoom: 7.3,
        minZoom: 7,
        maxZoom: 19,
    }),
});

/* 
------------------------------- 마커 설정 시작 ------------------------------- 
*/

// 마커의 종류
const markerTypes = ["tunnel", "bridge", "frozen"];
// 마커 타입 이름을 마커를 저장할 레이어로 변환하는 함수
const typeToLayer = (type) =>
    new ol.layer.Vector({
        source: new ol.source.Vector(),
        style: new ol.style.Style({
            image: new ol.style.Icon({
                scale: 0.05,
                src: `img/${type}.png`,
            }),
        }),
        zIndex: 1,
    });
// 마커를 담을 레이어의 집합
const markerLayers = arrToObj(markerTypes, typeToLayer);
// markerLayers의 각 레이어를 map에 추가
markerTypes.forEach((markerType) => map.addLayer(markerLayers[markerType]));

// 주어진 좌표에 주어진 id를 갖는 마커 생성
const createMarker = (coord, id) => {
    const { latitude, longitude } = coord;
    const geometry = new ol.geom.Point(
        ol.proj.fromLonLat([parseFloat(longitude), parseFloat(latitude)])
    );
    return new ol.Feature({ geometry, id });
};

// tunnel, bridge, frozen 중 하나를 인자로 제공한 경우
// 각각 터널, 교량, 결빙 상태를 지도에 마커로 표시
const setMarkers = async (markerType) => {
    // 인자로 설정한 이름이 invalid한 이름이거나
    // 마커로 설정할 필요가 없는 경우 return
    if (!markerTypes.includes(markerType)) return;
    const $option = document.getElementById(markerType);
    if (!$option || !$option.checked) return;

    try {
        // 마커를 담을 레이어를 생성
        const markerLayer = markerLayers[markerType];

        // api 호출 후 데이터를 파싱
        const res = await fetch(`http://localhost:8080/hazard/${markerType}`);
        const data = await res.json();
        if (data.error) throw data.error;

        // api 호출을 통해 얻어낸 데이터를 이용해 마커를 생성
        data.forEach((coord, i) =>
            markerLayer.getSource().addFeature(createMarker(coord, i))
        );
    } catch (error) {
        console.error(error);
    }
};

/* 
------------------------------- 마커 설정 끝 ------------------------------- 

------------------------------- 레이어 설정 시작 ------------------------------- 
*/

const { checkLayer, getLayer, createLayer } = (() => {
    const layers = {};

    const checkLayer = (cityId) => cityId in layers;
    const getLayer = (cityId) => layers[cityId];
    const createLayer = (cityId) =>
        (layers[cityId] = new ol.layer.Tile({
            visible: true,
            source: new ol.source.TileWMS({
                url: "http://localhost:8600/geoserver/geor/wms", //행정구역 16개 따로?
                params: {
                    FORMAT: "image/png",
                    TILED: true,
                    LAYERS: "geor:road",
                    CQL_FILTER: `sig_cd = ${cityId}`,
                },
            }),
        }));

    return { checkLayer, getLayer, createLayer };
})();

// 서버에서 hillShade 알고리즘을 실행
const runHillShade = async () => {
    // 특정 id를 지닌 input의 value를 각각 그 id의 value값으로 저장
    const data = arrToObj(
        ["address", "date", "time"],
        (id) => document.getElementById(id).value
    );

    const res = await fetch("/hillShade", {
        method: "POST",
        headers: { "Content-Type": "application/json; charset=UTF-8" },
        body: JSON.stringify(data),
    });
    const { latitude, longitude, cityId } = await res.json();

    // 검색한 지역 쪽으로 지도를 이동
    map.getView().setCenter([parseFloat(latitude), parseFloat(longitude)]);
    map.getView().setZoom(16);

    // 이전에 해당 도시를 검색한 적이 있다면 검색 결과 레이어를 제거
    if (checkLayer(cityId)) map.removeLayer(getLayer(cityId));
    // 검색 결과를 레이어로 바꾸어 map에 추가
    map.addLayer(createLayer(cityId));
};

/* 
------------------------------- 레이어 설정 끝 -------------------------------

------------------------------- 프로젝트 설정 시작 ------------------------------- 
*/

// 검색할 날짜의 범위를 제한
const inputDataRange = () => {
    const $date = document.getElementById("date");

    // 오늘 날짜
    const today = new Date();
    // 일주일 뒤
    const weekLater = new Date(today.getTime() + 7 * 24 * 60 * 60 * 1000);

    const dateToString = (date) => date.toISOString().split("T")[0];

    $date.valueAsDate = today;
    $date.min = dateToString(today);
    $date.max = dateToString(weekLater);
};

// 모달창을 켜거나 끔
const setLoading = (toggle) => {
    const $loading = document.getElementById("loading");
    const MODAL_HIDDEN = "modal-hidden";

    $loading.classList.toggle(MODAL_HIDDEN, !toggle);
};

// 사용자가 입력한 값을 이용해 서버에 특정 로직을 요청
const onSubmit = async (e) => {
    e.preventDefault();

    // 로딩창을 노출
    setLoading(true);

    // 이전에 생성한 마커 레이어 제거
    markerTypes.forEach((name) => markerLayers[name].getSource().clear());
    // hillShade 알고리즘 실행
    await runHillShade();
    // (교량, 터널, 상습결빙구역) -> 마커 생성
    await Promise.all(markerTypes.map(setMarkers));

    // 로딩창을 제거
    setLoading(false);
};

const init = () => {
    const $form = document.getElementById("form");
    $form.onsubmit = onSubmit;

    // 날짜 범위 설정
    inputDataRange();
};

init();

/* 
------------------------------- 프로젝트 설정 끝 ------------------------------- 
*/
