import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup} from '@angular/forms';

@Component({
  selector: 'app-report',
  templateUrl: './report.component.html',
  styleUrls: ['./report.component.scss']
})
export class ReportComponent implements OnInit {
  reportForm: FormGroup;

  constructor() { }

  ngOnInit() {
    this.reportForm = new FormGroup({
      venueName: new FormControl(''),
      criteria: new FormControl('')
    });
  }

}
