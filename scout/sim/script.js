function undraw()
{
	var canvas = document.getElementById("canvas");
	var ctx = canvas.getContext("2d");
	ctx.clearRect(0, 0, canvas.width, canvas.height);
}

function draw_grid(min_x, min_y, max_x, max_y, rows, cols)
{
	var canvas = document.getElementById("canvas");
	var ctx = canvas.getContext("2d");
	if (min_x < 0 || max_x > canvas.width)
		throw "Invalid x-axis bounds: " + min_x + " - " + max_x;
	if (min_y < 0 || max_y > canvas.height)
		throw "Invalid y-axis bounds: " + min_y + " - " + max_y;
    // draw vertical lines
    for (var col = 0 ; col <= cols ; ++col) {
        ctx.beginPath();
        ctx.moveTo(min_x + col * (max_x - min_x) / cols, min_y);
        ctx.lineTo(min_x + col * (max_x - min_x) / cols, max_y);
        ctx.closePath();
        ctx.lineWidth = 2;
        ctx.strokeStyle = "grey";
        ctx.stroke();
    }
    // draw horizontal lines
    for (var row = 0 ; row <= rows ; ++row) {
        ctx.beginPath();
        ctx.moveTo(min_x, min_y + row * (max_y - min_y) / rows);
        ctx.lineTo(max_x, min_y + row * (max_y - min_y) / rows);
        ctx.closePath();
        ctx.lineWidth = 2;
        ctx.strokeStyle = "grey";
        ctx.stroke();
    }
}

function rand(n) {
    return Math.floor((Math.random() * n));
}

function draw_dots(min_x, min_y, max_x, max_y, rows, cols, num, xcoords, ycoords, color, IDs) {
    var ctx = document.getElementById('canvas').getContext('2d');
    for(var i = 0 ; i < num ; ++ i) {
        ctx.beginPath();
        //ctx.moveTo(min_x + xcoords[i] * (max_x - min_x) / cols,
        //    min_y + ycoords[i] * (max_y - min_y) / rows );
        var locationx = min_x + xcoords[i] * (max_x - min_x) / cols + (max_x - min_x) / (4*cols)+ rand((max_x - min_x) / (2*cols));
        var locationy = min_y + ycoords[i] * (max_y - min_y) / rows+ (max_y - min_y) / (4*rows) + rand((max_y - min_y) / (2*rows));
        var radius = 3;
        if(rows > 50) radius = 2;
        ctx.arc(
            locationx, 
            locationy, 
            radius, 0, 2 * Math.PI);
        ctx.fillStyle = color;
        ctx.fill();
        if(IDs != null) {
            ctx.font = "10px Arial";
            ctx.textAlign = "left";
            ctx.lineWidth = 1;
            ctx.strokeStyle = "black";
            ctx.strokeText(IDs[i],        locationx + 1, locationy - 1);
        }
    }
}

function draw_landmarks(min_x, min_y, max_x, max_y, rows, cols, num, xcoords, ycoords, color) {
    var ctx = document.getElementById('canvas').getContext('2d');
    var width = (max_x - min_x) / (cols);
    var height = (max_y - min_y) / rows;
    for(var i = 0 ; i < num ; ++ i) {
        var offset_x = min_x + width*xcoords[i];
        var offset_y = min_y + height*ycoords[i];
        ctx.beginPath();
        ctx.moveTo(offset_x + width/2, offset_y + height/4);
        ctx.lineTo(offset_x + width/4, offset_y + 3*height/4);
        ctx.lineTo(offset_x + 3*width/4, offset_y + 3* height/4);
        ctx.closePath();
        ctx.lineWidth = 4;
        ctx.strokeStyle = "black";
        ctx.stroke();
        ctx.fillStyle=color;
        ctx.fill();
    }
}

function draw_outpost(min_x, min_y, max_x, max_y, rows, cols) {
    var ctx = document.getElementById('canvas').getContext('2d');
    var width = (max_x - min_x) / (cols);
    var height = (max_y - min_y) / rows;
    ctx.beginPath();
    offset_x = min_x;
    offset_y = min_y;
    ctx.moveTo(offset_x + width/4, offset_y + height/4);
    ctx.lineTo(offset_x + width/2, offset_y);
    ctx.lineTo(offset_x + 3*width/4, offset_y + height/4);
    ctx.lineTo(offset_x + 3*width/4, offset_y + 3*height/4);
    ctx.lineTo(offset_x + width/4, offset_y + 3*height/4);
    ctx.closePath();
    ctx.lineWidth = 4;
    ctx.strokeStyle = "black";
    ctx.stroke();
    ctx.fillStyle="yellow";
    ctx.fill();


    ctx.beginPath();
    offset_x = max_x - width;
    offset_y = min_y;
    ctx.moveTo(offset_x + width/4, offset_y + height/4);
    ctx.lineTo(offset_x + width/2, offset_y);
    ctx.lineTo(offset_x + 3*width/4, offset_y + height/4);
    ctx.lineTo(offset_x + 3*width/4, offset_y + 3*height/4);
    ctx.lineTo(offset_x + width/4, offset_y + 3*height/4);
    ctx.closePath();
    ctx.lineWidth = 4;
    ctx.strokeStyle = "black";
    ctx.stroke();
    ctx.fillStyle="yellow";
    ctx.fill();


    ctx.beginPath();
    offset_x = min_x;
    offset_y = max_y - height;
    ctx.moveTo(offset_x + width/4, offset_y + height/4);
    ctx.lineTo(offset_x + width/2, offset_y);
    ctx.lineTo(offset_x + 3*width/4, offset_y + height/4);
    ctx.lineTo(offset_x + 3*width/4, offset_y + 3*height/4);
    ctx.lineTo(offset_x + width/4, offset_y + 3*height/4);
    ctx.closePath();
    ctx.lineWidth = 4;
    ctx.strokeStyle = "black";
    ctx.stroke();
    ctx.fillStyle="yellow";
    ctx.fill();


    ctx.beginPath();
    offset_x = max_x - width;
    offset_y = max_y - height;
    ctx.moveTo(offset_x + width/4, offset_y + height/4);
    ctx.lineTo(offset_x + width/2, offset_y);
    ctx.lineTo(offset_x + 3*width/4, offset_y + height/4);
    ctx.lineTo(offset_x + 3*width/4, offset_y + 3*height/4);
    ctx.lineTo(offset_x + width/4, offset_y + 3*height/4);
    ctx.closePath();
    ctx.lineWidth = 4;
    ctx.strokeStyle = "black";
    ctx.stroke();
    ctx.fillStyle="yellow";
    ctx.fill();
}

function draw_shape(min_x, min_y, max_x, max_y, rows, cols, buildings, points, colors, types, highlight)
{
	var canvas = document.getElementById("canvas");
	var ctx = canvas.getContext("2d");
	if (min_x < 0 || max_x > canvas.width)
		throw "Invalid x-axis bounds: " + min_x + " - " + max_x;
	if (min_y < 0 || max_y > canvas.height)
		throw "Invalid y-axis bounds: " + min_y + " - " + max_y;
    // draw boxes
    for (var i = 0 ; i != points.length ; ++i) {
        var color = colors[types[i]];
        var diagonals = highlight && (i + 1 == points.length);
        var coord = points[i].split(",");
        var row = parse_int(coord[0]);
        var col = parse_int(coord[1]);
        if (row < 0 || row >= rows)
            throw "Invalid shape point row: " + row;
        if (col < 0 || col >= cols)
            throw "Invalid shape point col: " + col;
        var x1 = min_x + (col + 0) * (max_x - min_x) / cols + 1;
        var x2 = min_x + (col + 1) * (max_x - min_x) / cols - 1;
        var y1 = min_y + (row + 0) * (max_y - min_y) / rows + 1;
        var y2 = min_y + (row + 1) * (max_y - min_y) / rows - 1;
        ctx.beginPath();
        ctx.moveTo(x1, y1);
        ctx.lineTo(x1, y2);
        ctx.lineTo(x2, y2);
        ctx.lineTo(x2, y1);
        ctx.closePath();
        ctx.lineWidth = 2;
        ctx.strokeStyle = "black";
        ctx.stroke();
        if (color != null) {
            ctx.fillStyle = color;
            ctx.fill();
        }
        if (diagonals == true) {
            ctx.beginPath();
            ctx.moveTo(x1, y1);
            ctx.lineTo(x2, y2);
            ctx.stroke();
            ctx.beginPath();
            ctx.moveTo(x1, y2);
            ctx.lineTo(x2, y1);
            ctx.stroke();
        }
    }

    for(var i = 0; i != buildings.length; ++i) {
        var building = buildings[i];
        var color = colors[building.type];

        //Convert tiles to acceptable format:
        var tiles = [];
        for(var tile_index in building.tiles) {
            var coord = building.tiles[tile_index].split(",");
            var row = parse_int(coord[0]);
            var col = parse_int(coord[1]);

            var x1 = min_x + (col + 0) * (max_x - min_x) / cols + 1;
            var x2 = min_x + (col + 1) * (max_x - min_x) / cols - 1;
            var y1 = min_y + (row + 0) * (max_y - min_y) / rows + 1;
            var y2 = min_y + (row + 1) * (max_y - min_y) / rows - 1;
            tiles.push({row: row, col: col, x1: x1, x2: x2, y1: y1, y2: y2});
        }

        //Draw tiles as normal
        for(var tile_index in tiles) {
            var tile = tiles[tile_index];
            if (tile.row < 0 || tile.row >= rows)
                throw "Invalid shape point row: " + tile.row;
            if (tile.col < 0 || tile.col >= cols)
                throw "Invalid shape point col: " + tile.col;
            ctx.beginPath();
            ctx.moveTo(tile.x1, tile.y1);
            ctx.lineTo(tile.x1, tile.y2);
            ctx.lineTo(tile.x2, tile.y2);
            ctx.lineTo(tile.x2, tile.y1);
            ctx.closePath();
            ctx.lineWidth = 2;
            ctx.strokeStyle = color;//"black";
            ctx.stroke();
            if (color != null) {
                ctx.fillStyle = color;
                ctx.fill();
            }
        }


        //Collect all valid border points
        var borders = [];
        for(var x = 0; x != tiles.length; ++x) {
            var topOccluded = false, bottomOccluded = false, leftOccluded = false, rightOccluded = false;
            var tile = tiles[x];
            for(var y = 0; y != tiles.length; ++y) {
                var neighbor = tiles[y];
                if(tile.row + 1 == neighbor.row && neighbor.col == tile.col)
                    bottomOccluded = true;
                if(tile.row - 1 == neighbor.row && neighbor.col == tile.col)
                    topOccluded = true;
                if(tile.row == neighbor.row && neighbor.col == tile.col + 1)
                    rightOccluded = true;
                if(tile.row == neighbor.row && neighbor.col == tile.col - 1)
                    leftOccluded = true;
            }

            if(!rightOccluded)
                borders.push({x1: tile.x2, x2: tile.x2, y1: tile.y1, y2: tile.y2});
            if(!leftOccluded)
                borders.push({x1: tile.x1, x2: tile.x1, y1: tile.y1, y2: tile.y2});
            if(!topOccluded)
                borders.push({x1: tile.x1, x2: tile.x2, y1: tile.y1, y2: tile.y1});
            if(!bottomOccluded)
                borders.push({x1: tile.x1, x2: tile.x2, y1: tile.y2, y2: tile.y2});
        }

        //Draw Borders
        for(var border_index in borders) {
            var border = borders[border_index];
            ctx.beginPath();
            ctx.moveTo(border.x1, border.y1);
            ctx.lineTo(border.x2, border.y2);
            ctx.closePath();
            ctx.lineWidth = 2;
            ctx.strokeStyle = "white";
            ctx.stroke();
        }

    }
}

function draw_side(min_x, min_y, max_x, max_y, group, turns, colors)
{
	var canvas = document.getElementById("canvas");
	var ctx = canvas.getContext("2d");
	if (min_x < 0 || max_x > canvas.width)
		throw "Invalid x-axis bounds: " + min_x + " - " + max_x;
	if (min_y < 0 || max_y > canvas.height)
		throw "Invalid y-axis bounds: " + min_y + " - " + max_y;
    // draw message
    ctx.font = "32px Arial";
    ctx.textAlign = "left";
    ctx.lineWidth = 4;
    ctx.strokeStyle = "darkgrey";
    ctx.strokeText("Player: " + group,        min_x, min_y + 30);
    ctx.strokeText("Turns left: " + turns,         min_x, min_y + 60);
    // ctx.strokeText("CPU time: " + cpu + " s", min_x, min_y + 90);
    // ctx.strokeText("Legend:", min_x, min_y + 150);
    ctx.fillStyle = "darkblue";
    ctx.fillText("Player: " + group,        min_x, min_y + 30);
    ctx.fillText("Turns left: " + turns,         min_x, min_y + 60);
}

function parse_int(x)
{
	if (isNaN(parseFloat(x)) || !isFinite(x))
		throw "Not a number: " + x;
	var n = +x;
	if (n != Math.round(n))
		throw "Not an integer: " + n;
	return Math.round(n);
}

function parse_points(data)
{
	if (data.length % 2 != 0)
		throw "Invalid length: " + data.length;
	var points = new Array(data.length / 2);
	for (var i = 0 ; i != points.length ; ++i) {
		var x = parse_int(data[i + i + 0]);
		var y = parse_int(data[i + i + 1]);
		points[i] = [x, y];
	}
	return points;
}

function process(data)
{
    // parse data
    data = data.split(",");
    var group = data[0];
    var n = Number(data[1]);
    var turns_left = Number(data[2]);
    var refresh = Number(data[3]);
    var s = Number(data[4]);
    var scoutx = new Array(s);
    var scouty = new Array(s);
    for(var i = 0; i < s ; ++i) {
        scoutx[i] = Number(data[5 + 2 * i]);
        scouty[i] = Number(data[6 + 2 * i]);
    }
    var e = Number(data[5 + 2*s]);
    var enemyx = new Array(e);
    var enemyy = new Array(e);
    for(var i = 0; i < e ; ++i) {
        enemyx[i] = Number(data[6 + 2*s + 2 * i]);
        enemyy[i] = Number(data[7 + 2*s + 2 * i]);
    }

    var landmarkCount = Number(data[6 + 2*s + 2*e]);
    var landmarkx = new Array(landmarkCount);
    var landmarky = new Array(landmarkCount);
    for(var i = 0; i < landmarkCount ; ++i) {
        landmarkx[i] = Number(data[7 + 2*s + 2*e  +2 * i]);
        landmarky[i] = Number(data[8 + 2*s + 2*e  +2 * i]);
    }

    var scoutIDs = new Array(s);

    for(var i = 0 ; i < s ; ++ i) {
        scoutIDs[i] = Number(data[7 + 2*s + 2*e + 2*landmarkCount + i]);
    }

    console.log("data", data);
    if (refresh < 0.0) refresh = -1;
    else refresh = Math.round(refresh);

    // draw grid
    undraw();
    draw_grid(300, 50, 900, 650, n+2, n+2, "black");
    // draw for 1st player
    var colors = ["orange", "black", "purple", "green", "blue"];
    draw_outpost(300, 50, 900, 650, n+2, n+2);
    console.log("count: " + landmarkCount);
    draw_landmarks(300, 50, 900, 650, n+2, n+2, landmarkCount, landmarkx, landmarky, "limegreen")
    draw_dots(300, 50, 900, 650, n+2, n+2, s, scoutx, scouty, "blue", scoutIDs);
    draw_dots(300, 50, 900, 650, n+2, n+2, e, enemyx, enemyy, "red", null);
    draw_side ( 10,  40,  190, 690, group, turns_left, colors);
    //draw_outpost(250, 50, 850, 650 , n+2, n+2);
    //draw_shape(250,  50,  850, 650, 50, 50, buildings, cuts, colors, types, highlight == 0);
    return refresh;
}

var latest_version = -1;

function ajax(version, retries, timeout)
{
	var xhr = new XMLHttpRequest();
	xhr.onload = (function() {
		var refresh = -1;
		try {
			if (xhr.readyState != 4)
				throw "Incomplete HTTP request: " + xhr.readyState;
			if (xhr.status != 200)
				throw "Invalid HTTP status: " + xhr.status;
			refresh = process(xhr.responseText);
			if (latest_version < version && paused == 0)
				latest_version = version;
			else
				refresh = -1;
		} catch (message) { alert(message); }
		if (refresh >= 0)
			setTimeout(function() { ajax(version + 1, 10, 100); }, refresh);
	});
	xhr.onabort   = (function() { location.reload(true); });
	xhr.onerror   = (function() { location.reload(true); });
	xhr.ontimeout = (function() {
		if (version <= latest_version)
			console.log("AJAX timeout (version " + version + " <= " + latest_version + ")");
		else if (retries == 0)
			location.reload(true);
		else {
			console.log("AJAX timeout (version " + version + ", retries: " + retries + ")");
			ajax(version, retries - 1, timeout * 2);
		}
	});
	xhr.open("GET", "data.txt", true);
	xhr.responseType = "text";
	xhr.timeout = timeout;
	xhr.send();
}

function pause() {
    paused = (paused + 1) % 2;
}

var paused = 0;
ajax(0, 10, 100);
