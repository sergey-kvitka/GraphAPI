class Vertex {
    constructor(
        name, x, y
    ) {
        this.name = name;
        this.x = x;
        this.y = y;
    }
}

let MAX_VERTEX_AMOUNT = 200
let CANVAS_SIZE = 800;
let VERTEX_SIZE = 80;
let EDGE_SIZE = 20;
let RADIUS = 330;
let ROUND = Math.PI * 2;
let EDGE_INPUT = 1;
let HIDE_WEIGHT = true;

let LOCALHOST = 5555;

let VERTICES = new Map();
let EDGES_AMOUNT = 0;

let IMAGE_INDEX_TO_VERTEX_NAME = new Map();

updateGraph().then();

function getUrl(link) {
    return `http://localhost:${LOCALHOST}/graph/${link}`;
}

function currentEdgeInput() {
    let input = EDGE_INPUT;
    EDGE_INPUT = (EDGE_INPUT === 1) ? 2 : 1;
    return input;
}

function writeInputOnclick(vertexNumber) {
    let i = currentEdgeInput();
    document.getElementById(`vertexOfNewEdge${i}`).value = +vertexNumber;
    document.getElementById(`vertexOfEdgeToDelete${i}`).value = +vertexNumber;
}

function writeDeleteVertexInput(vertexNumber) {
    document.getElementById(
        `vertexToDelete`
    ).value = +vertexNumber;
}

function writeWeightInput(weight) {
    document.getElementById('weightOfEdge').value = +weight;
}

function toggleEdgesWeight() {
    HIDE_WEIGHT = !HIDE_WEIGHT;
    document.getElementsByName('edge_weight')
        .forEach(weight => {
                weight.classList.toggle('display-none');
            }
        );
}

async function getVerticesFromAPI() {
    const response = await fetch(getUrl('get_vertices'));
    return await response.json();
}

async function getEdgesFromAPI() {
    const response = await fetch(getUrl('get_edges'));
    return await response.json();
}

async function addVertex() {
    if (VERTICES.size >= MAX_VERTEX_AMOUNT) {
        alert(`Максимальное допустимое число вершин: ${MAX_VERTEX_AMOUNT}`);
        return;
    }
    fetch(getUrl('add_vertex'), {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({})
        }
    ).then(() => updateGraph());
}

async function addEdge() {
    let value = document.getElementById('weightOfEdge').value;
    fetch(getUrl(`add_edge/${
            document.getElementById('vertexOfNewEdge1').value
        }/${
            document.getElementById('vertexOfNewEdge2').value
        }`),
        {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: (value === '' || value === '0') ? '1' : value
        }
    ).then(() => updateGraph());
}

async function deleteLastVertex() {
    fetch(getUrl('delete_last_vertex'), {
        method: 'DELETE'
    }).then(() => updateGraph());
}

async function deleteDefiniteVertex() {
    fetch(getUrl('delete_vertex/' +
        document.getElementById('vertexToDelete').value
    ), {
        method: 'DELETE'
    }).then(() => updateGraph());
}

async function deleteEdge() {
    fetch(getUrl(`delete_edge/${
        document.getElementById('vertexOfEdgeToDelete1').value
    }/${
        document.getElementById('vertexOfEdgeToDelete2').value
    }`), {
        method: 'DELETE'
    }).then(() => updateGraph());

}

async function updateGraph() {
    document.getElementById('canvas').innerHTML = '';

    getVerticesFromAPI().then(
        vertices => showVertices(vertices)
    ).then(() =>
        getEdgesFromAPI().then(
            edges => {
                showEdges(edges);
                showInfo();
            }
        ));
}

async function clearGraph() {
    fetch(getUrl('clear_graph'), {
        method: 'DELETE'
    }).then(() => updateGraph());
}

async function completeGraph() {
    fetch(getUrl('complete_graph'), {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({})
        }
    ).then(() => {
        updateGraph()
    });
}

async function setWeight() {
    fetch(getUrl(`set_edge_weight/${
        document.getElementById('vertexOfNewEdge1').value
    }/${
        document.getElementById('vertexOfNewEdge2').value
    }`), {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(document.getElementById('weightOfEdge').value)
    }).then(() => {
        updateGraph()
    });
}

async function deleteEdges() {
    fetch(getUrl('delete_all_edges'), {
            method: 'DELETE'
        }
    ).then(() => updateGraph());
}

function createVertexWeb(vertexName, vertexIndex, x, y, imgIndex) {
    let vertex = document.createElement('div');
    vertex.classList.add('vertex', 'flex-center');
    vertex.setAttribute('style', vertexCoordinates(x, y) +
        `\n background-image: url("img/mushroom${imgIndex}.png");`);
    vertex.innerHTML = `<p>${vertexName}</p>`;
    vertex.id = `vertex_${vertexName}`;

    vertex.addEventListener("click", () => {
        writeInputOnclick(vertexName);
        writeDeleteVertexInput(vertexName);
    });

    document.getElementById('canvas').appendChild(vertex);
}

function showVertices(verticesToShow) {
    let verticesAmount = verticesToShow.length;
    VERTICES = new Map();
    let center = CANVAS_SIZE / 2;

    for (let i = 0; i < verticesAmount; i++) {
        let x = Math.sin(ROUND / verticesAmount * i) * RADIUS + center;
        let y = Math.cos(ROUND / verticesAmount * i) * RADIUS + center;
        let vertex = verticesToShow[i];
        let imgIndex = IMAGE_INDEX_TO_VERTEX_NAME.get(vertex.name);
        if (imgIndex == null) {
            imgIndex = Math.ceil(Math.random() * 4);
        }
        createVertexWeb(vertex.name, i, x, y, imgIndex);
        VERTICES.set(vertex.name, new Vertex(vertex.name, x, y));
        IMAGE_INDEX_TO_VERTEX_NAME.set(vertex.name, imgIndex);
    }
    for (let key of IMAGE_INDEX_TO_VERTEX_NAME.keys()) {
        if (VERTICES.get(key) == null) {
            IMAGE_INDEX_TO_VERTEX_NAME.delete(key);
        }
    }
}

function vertexCoordinates(x, y) {
    return `
        left: ${vertexXToCanvas(x)}px;
        top: ${vertexYToCanvas(y)}px;
    `;
}

function vertexXToCanvas(x) {
    return xToCanvas(x) - VERTEX_SIZE / 2;
}

function vertexYToCanvas(y) {
    return yToCanvas(y) - VERTEX_SIZE / 2;
}

function xToCanvas(x) {
    return x;
}

function yToCanvas(y) {
    return CANVAS_SIZE - y;
}


function createEdgeWeb(x, y, length, weight, angle, v1_name, v2_name) {
    let edge = document.createElement('hr');
    edge.classList.add('edge');
    edge.setAttribute('style', `
        top: ${(y - (EDGE_SIZE / 2)) - EDGE_SIZE - 1};
        left: ${(x - length / 2) + EDGE_SIZE / 2 - 1};  
        transform: rotate(${angle}rad);
        width: ${length};
    `);
    edge.addEventListener("click", () => {
        writeInputOnclick(v1_name);
        writeInputOnclick(v2_name);
        writeWeightInput(weight);
    });

    let edgeWeight = document.createElement('div');
    edgeWeight.classList.add('edgeWeight');
    edgeWeight.innerHTML = weight;
    edgeWeight.setAttribute('style', `
         transform: rotate(${-angle}rad) translateY(-50%);
         left: ${length * 0.4};
         top: ${-(EDGE_SIZE / 2)};
         z-index: 4;
    `);
    edgeWeight.setAttribute('name', 'edge_weight');
    if (HIDE_WEIGHT) {
        edgeWeight.classList.add('display-none');
    }

    document.getElementById('canvas').appendChild(edge);
    edge.appendChild(edgeWeight);
}

function showEdges(edges) {
    let edgesAmount = edges.length;
    EDGES_AMOUNT = edgesAmount;
    let x1, x2, y1, y2;
    let v1, v2;
    let length, angle, x, y;
    let a, b;

    for (let i = 0; i < edgesAmount; i++) {
        v1 = VERTICES.get(edges[i]["v1"].name);
        v2 = VERTICES.get(edges[i]["v2"].name);
        x1 = xToCanvas(v1.x);
        y1 = yToCanvas(v1.y);
        x2 = xToCanvas(v2.x);
        y2 = yToCanvas(v2.y);
        a = y1 - y2;
        b = x1 - x2;
        length = Math.sqrt(a * a + b * b);
        angle = Math.atan(a / b);
        x = (x1 + x2) / 2;
        y = (y1 + y2) / 2;
        createEdgeWeb(x, y, length, edges[i]['weight'], angle, v1.name, v2.name);
    }
}

function showInfo() {
    document.getElementById('info').innerHTML = `Вершин: ${VERTICES.size}; Рёбер: ${EDGES_AMOUNT}`;
}

