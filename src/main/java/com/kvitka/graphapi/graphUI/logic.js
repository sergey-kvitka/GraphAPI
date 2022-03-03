class Vertex {
    constructor(
        name, x, y
    ) {
        this.name = name;
        this.x = x;
        this.y = y;
    }
}

let canvasSize = 800;
let vertexSize = 30;
let radius = 330;
let round = Math.PI * 2;
let edgeInput = 1;
let hideWeight = false;

let localhost = 5555;

let vertices = new Map();

updateGraph().then();

function getUrl(link) {
    return `http://localhost:${localhost}/graph/${link}`;
}

function currentEdgeInput() {
    let input = edgeInput;
    edgeInput = (edgeInput === 1) ? 2 : 1;
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
    hideWeight = !hideWeight;
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

async function postMappingWithFilePath(url) {
    fetch(getUrl(url), {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(document.getElementById('filepathInput').value)
        }
    ).then(() => {
        updateGraph()
    });
}

async function loadGraph_matrix() {
    await postMappingWithFilePath('create_graph_by_adjacency_matrix');
}

async function loadGraph_edgeList() {
    await postMappingWithFilePath('create_graph_by_edge_list');
}

async function saveGraph_matrix() {
    await postMappingWithFilePath('write_adjacency_matrix_into_file');
}

async function saveGraph_edgeList() {
    await postMappingWithFilePath('write_edge_list_into_file');
}

async function deleteEdges() {
    fetch(getUrl('delete_all_edges'), {
            method: 'DELETE'
        }
    ).then(() => updateGraph());
}

async function additionalTask() {
    fetch(getUrl('has_edge_with_vertices_with_3_adjacent')).then(result1 => {
        result1 = result1.json();
        result1.then(result => {
            console.log(result)
            let str = '';
            let length = result.length;
            for (let i = 0; i < length; i++) {
                str += `${result[i]['v1'].name} ${result[i]['v2'].name}\n`;
            }
            if (str === '') alert('Результат отрицательный');
            else alert(str);
        })})
}

function createVertexWeb(vertexName, vertexIndex, x, y) {
    let vertex = document.createElement('div');
    vertex.classList.add('vertex', 'flex-center');
    vertex.setAttribute('style', vertexCoordinates(x, y));
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
    vertices = new Map();
    let center = canvasSize / 2;

    for (let i = 0; i < verticesAmount; i++) {
        let x = Math.sin(round / verticesAmount * i) * radius + center;
        let y = Math.cos(round / verticesAmount * i) * radius + center;
        createVertexWeb(verticesToShow[i].name, i, x, y);
        vertices.set(verticesToShow[i].name, new Vertex(verticesToShow[i].name, x, y));
    }
}

function vertexCoordinates(x, y) {
    return `
        left: ${vertexXToCanvas(x)}px;
        top: ${vertexYToCanvas(y)}px;
    `;
}

function vertexXToCanvas(x) {
    return xToCanvas(x) - vertexSize / 2;
}

function vertexYToCanvas(y) {
    return yToCanvas(y) - vertexSize / 2;
}

function xToCanvas(x) {
    return x;
}

function yToCanvas(y) {
    return canvasSize - y;
}


function createEdgeWeb(x, y, length, weight, angle, v1_name, v2_name) {
    let edgeSize = 4;//px

    let edge = document.createElement('hr');
    edge.classList.add('edge');
    edge.setAttribute('style', `
        top: ${(y - (edgeSize / 2)) - edgeSize - 1};
        left: ${(x - length / 2) + edgeSize / 2 - 1};  
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
         top: ${-(edgeSize / 2)};
         z-index: 4;
    `);
    edgeWeight.setAttribute('name', 'edge_weight');
    if (hideWeight) {
        edgeWeight.classList.add('display-none');
    }

    document.getElementById('canvas').appendChild(edge);
    edge.appendChild(edgeWeight);
}

function showEdges(edges) {
    let edgesAmount = edges.length;
    edgesAmount_ = edgesAmount;
    let x1, x2, y1, y2;
    let v1, v2;
    let length, angle, x, y;
    let a, b;

    for (let i = 0; i < edgesAmount; i++) {
        v1 = vertices.get(edges[i]["v1"].name);
        v2 = vertices.get(edges[i]["v2"].name);
        x1 = xToCanvas(v1.x); y1 = yToCanvas(v1.y);
        x2 = xToCanvas(v2.x); y2 = yToCanvas(v2.y);
        a = y1 - y2;
        b = x1 - x2;
        length = Math.sqrt(a * a + b * b);
        angle = Math.atan(a / b);
        x = (x1 + x2) / 2;
        y = (y1 + y2) / 2;
        createEdgeWeb(x, y, length, edges[i]['weight'], angle, v1.name, v2.name);
    }
}

let edgesAmount_ = 0;
function showInfo() {
    document.getElementById('info').innerHTML = `Вершин: ${vertices.size}; Рёбер: ${edgesAmount_}`;
}

